package eid;

import io.vavr.test.Arbitrary;
import io.vavr.test.Gen;
import io.vavr.test.Property;
import org.junit.jupiter.api.Test;

class EIDProperties {
    private final Gen<Sex> sexGenerator = Gen.choose(Sex.values());
    private final Arbitrary<EID> validEID = sexGenerator.map(EID::new).arbitrary();

    // https://github.com/advent-of-craft/2024/blob/main/docs/day17/solution/step-by-step.md
    @Test
    void roundTrip() {
        Property.def("parseEID(eid.toString) == eid")
                .forAll(validEID)
                .suchThat(eid -> EID.parse(eid.toString()).contains(eid))
                .check()
                .assertIsSatisfied();
    }
}