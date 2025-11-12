package com.example.demo;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.StringDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.api.common.serialization.SimpleStringSchema;

import java.util.Properties;

/**
 * 独立Flink CDC任务主类，可打包为jar并提交到Flink集群
 * 用法示例（命令行提交）：
 * flink run -c com.example.demo.MyFlinkCdcJob your-job.jar --mysql.host localhost --mysql.port 3306 --mysql.user root --mysql.password yourpassword --mysql.db demo --mysql.table demo.your_table --kafka.servers localhost:9092 --kafka.topic demo-topic
 */
public class MyFlinkCdcJob {
    public static void main(String[] args) throws Exception {
        // 参数解析
        Properties params = ParameterTool.fromArgs(args).getProperties();
        String mysqlHost = params.getProperty("mysql.host", "localhost");
        int mysqlPort = Integer.parseInt(params.getProperty("mysql.port", "3306"));
        String mysqlUser = params.getProperty("mysql.user", "root");
        String mysqlPassword = params.getProperty("mysql.password", "yourpassword");
        String mysqlDb = params.getProperty("mysql.db", "demo");
        String mysqlTable = params.getProperty("mysql.table", "demo.your_table");
        String kafkaServers = params.getProperty("kafka.servers", "localhost:9092");
        String kafkaTopic = params.getProperty("kafka.topic", "demo-topic");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.enableCheckpointing(60000);

        // CDC Source
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname(mysqlHost)
                .port(mysqlPort)
                .databaseList(mysqlDb)
                .tableList(mysqlTable)
                .username(mysqlUser)
                .password(mysqlPassword)
                .deserializer(new StringDebeziumDeserializationSchema())
                .build();

        // Kafka Sink
        Properties kafkaProps = new Properties();
        kafkaProps.setProperty("bootstrap.servers", kafkaServers);
        FlinkKafkaProducer<String> kafkaProducer = new FlinkKafkaProducer<>(
                kafkaTopic,
                new SimpleStringSchema(),
                kafkaProps
        );

        env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL CDC Source")
                .addSink(kafkaProducer)
                .name("Kafka Sink");

        env.execute("Custom Flink CDC to Kafka Job");
    }
}