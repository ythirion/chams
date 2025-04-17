package eid;

import io.vavr.control.Either;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class EID {
    public static Either<ParsingError, EID> parse(String potentialEID) {
        return Either.right(new EID());
    }
}
