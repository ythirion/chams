package money_problem.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataTest {
    @Test
    void test() {
        var data = new Data("test");
        assertThat(data).isNotNull();
    }
}
