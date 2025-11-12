package com.example.demo;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class FlinkJobService {

    @Value("${flink.job.name:demo-flink-job}")
    private String jobName;

    @Value("${flink.job.parallelism:1}")
    private int parallelism;

    @Value("${flink.checkpoint.interval:60000}")
    private long checkpointInterval;

    @Value("${flink.state.backend:filesystem}")
    private String stateBackend;

    @Value("${flink.state.checkpoints.dir:file:///tmp/flink-checkpoints}")
    private String checkpointsDir;

    @Value("${flink.state.savepoints.dir:file:///tmp/flink-savepoints}")
    private String savepointsDir;

    /**
     * 业务示例：Flink CDC 读取MySQL变更，写入Kafka
     */
    @PostConstruct
    public void runFlinkCdcToKafkaJob() {
        try {
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            env.setParallelism(parallelism);
            env.enableCheckpointing(checkpointInterval);

            // Flink CDC MySQL Source 配置
            com.ververica.cdc.connectors.mysql.source.MySqlSource<String> mySqlSource =
                com.ververica.cdc.connectors.mysql.source.MySqlSource.<String>builder()
                    .hostname("localhost")
                    .port(3306)
                    .databaseList("demo") // 监控的数据库
                    .tableList("demo.your_table") // 监控的表，格式: db.table
                    .username("root")
                    .password("yourpassword")
                    .deserializer(new com.ververica.cdc.debezium.StringDebeziumDeserializationSchema())
                    .build();

            // Kafka Sink 配置
            java.util.Properties kafkaProps = new java.util.Properties();
            kafkaProps.setProperty("bootstrap.servers", "localhost:9092");
            String kafkaTopic = "demo-topic";
            org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer<String> kafkaProducer =
                new org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer<>(
                    kafkaTopic,
                    new org.apache.flink.api.common.serialization.SimpleStringSchema(),
                    kafkaProps
                );

            // 业务流：MySQL CDC -> Kafka
            env.fromSource(mySqlSource, org.apache.flink.api.common.eventtime.WatermarkStrategy.noWatermarks(), "MySQL CDC Source")
                .name("MySQL CDC Source")
                .addSink(kafkaProducer)
                .name("Kafka Sink");

            JobExecutionResult result = env.execute(jobName);
            System.out.println("Flink CDC->Kafka作业执行完成，耗时(ms): " + result.getNetRuntime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
