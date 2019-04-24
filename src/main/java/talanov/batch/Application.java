package talanov.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import talanov.batch.conf.RePlaceFileConf;
import talanov.batch.conf.ReStarAppConf;
import talanov.batch.conf.SourceToStageConf;
import talanov.batch.entity.Lang;
import talanov.batch.skippers.FileVerificationSkipper;

import java.io.File;

@SpringBootApplication
@EnableBatchProcessing
public class Application {
    private final SourceToStageConf sourceToStageConf;
    private final ReStarAppConf reStarApptStepConf;
    private final RePlaceFileConf rePlaceFileConf;
    private final FileVerificationSkipper fileVerificationSkipper;

    @Autowired
    public Application(SourceToStageConf sourceToStageConf,
                       ReStarAppConf reStarApptStepConf,
                        RePlaceFileConf rePlaceFileConf,
                       FileVerificationSkipper fileVerificationSkipper) {
        this.sourceToStageConf = sourceToStageConf;
        this.reStarApptStepConf = reStarApptStepConf;
        this.rePlaceFileConf = rePlaceFileConf;
        this.fileVerificationSkipper = fileVerificationSkipper;
    }

    public static void main(String[] args) {
        System.setProperty("input", "file://" +
                new File("/Users/dbryzhatov/Desktop/talanov_batch/src/main/resources/incoming/in.csv"));

        System.setProperty("output", "file://" +
                new File("/Users/dbryzhatov/Desktop/batch_cv_to_db/src/main/resources/out.csv"));

        SpringApplication.run(Application.class, args);
    }

    @Bean
    Job job(JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory) {

        Step reStartAppStep = stepBuilderFactory.get("reStart")
                .tasklet(reStarApptStepConf.reStart()).build();

        Step sourceToStageStep = stepBuilderFactory.get("sourceToStageStepConf")
                .<Lang, Lang>chunk(10)
                    .faultTolerant().skipPolicy(fileVerificationSkipper)
                    .reader(sourceToStageConf.fileReader(null))
                    .processor(sourceToStageConf.sourceToStageProcessor())
                    .writer(sourceToStageConf.jdbcWriter(null))
                .build();

        Step rePlaceFileStep = stepBuilderFactory
                .get("rePlace")
                .tasklet(rePlaceFileConf.replaceInputFile(null))
                .build();

        return jobBuilderFactory.get("talanov_task")
                .incrementer(new RunIdIncrementer())
                .preventRestart()
                    .start(reStartAppStep)
                    .next(sourceToStageStep)
                    .next(rePlaceFileStep)
                .build();
    }
}
