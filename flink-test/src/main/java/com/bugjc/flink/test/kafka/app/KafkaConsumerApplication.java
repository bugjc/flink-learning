package com.bugjc.flink.test.kafka.app;

import com.bugjc.flink.config.EnvironmentConfig;
import com.bugjc.flink.config.annotation.Application;
import com.bugjc.flink.connector.kafka.KafkaConsumerConfig;
import com.bugjc.flink.connector.kafka.KafkaProducerConfig;
import com.bugjc.flink.test.kafka.app.config.KafkaProducerConfig2;
import com.bugjc.flink.test.kafka.app.model.KafkaEvent;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

/**
 * 程序入口
 *
 * @author aoki
 * @date 2020/7/14
 **/
@Slf4j
@Application
public class KafkaConsumerApplication {

    /**
     * 测试流程：先启动当前程序，然后启动 com.bugjc.flink.test.kafka.app.KafkaProducerApplication 程序发送消息
     * 注意事项：如消费者配置的 topic 是正则表达式则需要先初始化（创建）一个可匹配的 topic。
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //1.环境参数配置
        EnvironmentConfig environmentConfig = new EnvironmentConfig(args);
        final StreamExecutionEnvironment env = environmentConfig.getStreamExecutionEnvironment();

        //2.获取 kafka 消费者配置
        KafkaConsumerConfig kafkaConsumerConfig = environmentConfig.getComponent(KafkaConsumerConfig.class);
        FlinkKafkaConsumer011<KafkaEvent> consumer011 = kafkaConsumerConfig.createKafkaSource(KafkaEvent.class);
        SingleOutputStreamOperator<KafkaEvent> kafkaEventSource = env
                .addSource(consumer011)
                .setParallelism(2);

        //3.打印消费的数据
        kafkaEventSource.print();


        //4. sink kafka
        //TODO 多个生产者消费者配置解析问题
        KafkaProducerConfig2 kafkaProducerConfig = environmentConfig.getComponent(KafkaProducerConfig2.class);
        FlinkKafkaProducer011<KafkaEvent> producer011 = kafkaProducerConfig.createKafkaSink(KafkaEvent.class);
        kafkaEventSource.addSink(producer011);

        //5.执行
        env.execute(KafkaConsumerApplication.class.getName());
    }
}