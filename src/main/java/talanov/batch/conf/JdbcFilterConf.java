package talanov.batch.conf;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import talanov.batch.entity.Lang;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class JdbcFilterConf {

    @Bean
    public ItemReader<Lang> reader() {
        return new JdbcCursorItemReaderBuilder<Lang>()
                .sql("SELECT * FROM \"lang\"")
                .rowMapper(new LangMapper())
                .build();
    }

    @Bean
    public ItemProcessor<Lang, Lang> processor() {
        return new ItemProcessor<Lang, Lang>() {
            @Override
            public Lang process(Lang item) throws Exception {

                return null;
            }
        };
    }

    private static class LangMapper implements RowMapper<Lang> {
        @Override
        public Lang mapRow(ResultSet resultSet, int i) throws SQLException {
            Lang lang = new Lang();
            lang.setId(resultSet.getLong("id"));
            lang.setBatchId(resultSet.getLong("batch_id"));
            lang.setName(resultSet.getString("name"));
            return lang;
        }
    }
}
