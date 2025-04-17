package eid;

import io.vavr.test.Arbitrary;
import io.vavr.test.Property;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EIDProperties {
    private final Arbitrary<EID> validEID = Arbitrary.of(new EID());

    @Test
    void roundTrip() {
        Property.def("parseEID(eid.toString) == eid")
                .forAll(validEID)
                .suchThat(eid -> EID.parse(eid.toString()).contains(eid))
                .check()
                .assertIsSatisfied();
    }
}