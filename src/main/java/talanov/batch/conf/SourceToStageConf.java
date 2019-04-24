package talanov.batch.conf;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import talanov.batch.entity.Lang;

import javax.sql.DataSource;
import javax.swing.*;

@Configuration
public class SourceToStageConf {

    @Bean
    public ItemReader<Lang> fileReader(@Value("${input}") Resource resource) {
        return new FlatFileItemReaderBuilder<Lang>()
                .name("file-reader")
                .resource(resource)
                .targetType(Lang.class)
                .delimited().delimiter("\n").names(new String[]{"name"})
                .build();
    }

    @Bean
    public ItemProcessor<Lang, Lang> sourceToStageProcessor() {
        return new Process();
    }

    @Bean
    public ItemWriter<Lang> jdbcWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Lang>()
                .dataSource(dataSource)
                .sql("INSERT INTO \"lang\" (name, batch_id) VALUES (:name, :batchId)")
                .beanMapped()
                .build();
    }

    private static class Process implements ItemProcessor<Lang, Lang>, StepExecutionListener {
        private long batchId;

        @Override
        public Lang process(Lang item) throws Exception {
            if(item.getName().equals("-")){
                return null;
            }
            item.setBatchId(batchId);
            return item;
        }

        @Override
        public void beforeStep(StepExecution stepExecution) {
            batchId = stepExecution.getJobExecution().getJobId();
        }

        @Override
        public ExitStatus afterStep(StepExecution stepExecution) {
            return ExitStatus.COMPLETED;
        }
    }
}
