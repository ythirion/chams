package org.lotr.kata;

public class MiddleEarthItem {
    public String n;
    public int q;
    public int qual;
    public int p = 0;
    public ItemOrigin o;
    public boolean isRing = false;
    public boolean isGood = false;
    public boolean isE = false;
    public boolean isMithril = false;
    public boolean isC = false;
    public int m = 0;

    public MiddleEarthItem(String name, int quantity, int quality, ItemOrigin origin) {
        this.n = name;
        this.q = quantity;
        this.qual = quality;
        this.o = origin;

        if (name.contains("Ring") && origin == ItemOrigin.MORDOR) {
            this.isRing = true;
            if (name.contains("One")) {
                this.isE = true;
                this.p = 10000;
                this.m = 100;
            }
        }

        if (name.contains("Mithril")) {
            this.isMithril = true;
            this.p = 800;
        }

        if ((quality > 80) && origin == ItemOrigin.GONDOR) {
            this.p = quality * 2;
            this.isGood = true;
        } else if (quality > 65) {
            this.p = quality;
        } else {
            this.p = quality / 2;
        }

        if (name.contains("cursed")) {
            this.isC = true;
            this.p = this.p / 3;
        }
    }

    // Used by the shops to calculate price after haggle
    public int getFinalPrice(boolean isHaggling, String dayOfWeek) {
        int pr = 0;
        if (isHaggling && !this.isRing) {
            pr = (int) (this.p * 0.9);
        } else if (isHaggling && this.isRing) {
            pr = this.p;
        } else {
            pr = this.p;
        }

        if (dayOfWeek.equals("Sunday") || dayOfWeek.equals("Saturday")) {
            pr = (int) (pr * 1.1);
        }

        if (this.isC) {
            if (dayOfWeek.equals("Monday")) {
                pr = (int) (pr * 0.5);
            }
        }

        if (this.isMithril && (dayOfWeek.equals("Thursday") || dayOfWeek.equals("Friday"))) {
            pr = (int) (pr * 1.15);
        }

        if (this.isGood && dayOfWeek.equals("Wednesday")) {
            pr = (int) (pr * 0.95);
        }

        return pr;
    }

    public void updateQuality() {
        if (this.qual > 0) {
            if (!this.isGood) {
                this.qual = this.qual - 1;
            }
        }

        if (this.isGood) {
            if (this.qual < 50) {
                this.qual = this.qual + 1;
            }
        }

        if (this.isC) {
            this.qual = this.qual - 2;
            if (this.qual < 0) {
                this.qual = 0;
            }
        }

        if (this.isE) {
            // The One Ring doesn't change quality
        } else if (this.isRing) {
            if (this.qual > 80) {
                this.qual = this.qual - 1;
            }
        }

        if (this.isMithril) {
            if (this.qual < 90) {
                this.qual = this.qual + 1;
            }
        }

        if (this.o == ItemOrigin.MORDOR && !this.isRing) {
            this.qual = this.qual - 2;
            if (this.qual < 0) {
                this.qual = 0;
            }
        }

        // Magic items have magic levels
        if (this.m > 0) {
            this.m = this.m - 1;
        }
    }

    public double getRate() {
        if (this.isGood) {
            return 1.1;
        } else if (this.isMithril) {
            return 1.2;
        }
        return 1.0;
    }
}