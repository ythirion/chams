package eid;

import io.vavr.control.Either;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class EID {
    private final Sex sex;

    public EID(Sex validSex) {
        this.sex = validSex;
    }

    public static Either<ParsingError, EID> parse(String potentialEID) {
        return Sex.parse(potentialEID.charAt(0))
                .map(EID::new);
    }

    @Override
    public String toString() {
        return sex.value + "";
    }
}