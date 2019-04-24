package talanov.batch.conf;

import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class ReStarAppConf {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ReStarAppConf(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Bean
    public Tasklet reStart() {
        return (stepContribution, chunkContext) -> {
            jdbcTemplate.execute("DELETE FROM \"lang\"");
            return RepeatStatus.FINISHED;
        };
    }
}
