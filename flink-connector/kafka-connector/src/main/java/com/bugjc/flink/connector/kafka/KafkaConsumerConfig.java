package com.bugjc.flink.connector.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.bugjc.flink.config.AbstractConfig;
import com.bugjc.flink.config.annotation.ConfigurationProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * kafka 消费者属性配置
 *
 * @author aoki
 * @date 2020/7/2
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@ConfigurationProperties(prefix = "flink.kafka.consumer.")
public class KafkaConsumerConfig extends AbstractConfig implements Serializable {

    @JSONField(name = "bootstrap.servers")
    private String bootstrapServers;
    @JSONField(name = "zookeeper.connect")
    private String zookeeperConnect;
    @JSONField(name = "group.id")
    private String groupId;
    @JSONField(name = "key.deserializer")
    private String keyDeserializer;
    @JSONField(name = "value.deserializer")
    private String valueDeserializer;
    @JSONField(name = "auto.offset.reset")
    private String autoOffsetReset;
    @JSONField(name = "flink.partition-discovery.interval-millis")
    private String automaticPartition;

    /**
     * topic 值有三类，分别对应：单 topic、多 topic 和 topic 正则表达式消费数据的方式。
     * 例：单 topic --> testTopicName;多 topic --> testTopicName,testTopicName1;topic 正则表达式 --> testTopicName[0-9]
     */
    @JSONField(name = "topic")
    private String topic;

    /**
     * 获取 kafka 参数
     *
     * @return
     */
    @JSONField(serialize = false)
    private Properties getProperties() {
        return JSON.parseObject(JSON.toJSONString(this), Properties.class);
    }

    /**
     * 获取 kafka 序列化、反序列化器
     *
     * @param eventClass
     * @param <T>
     * @return
     */
    @JSONField(serialize = false)
    private <T> KafkaEventSchema<T> getKafkaEventSchema(Class<T> eventClass) {
        return new KafkaEventSchema<T>(eventClass);
    }

    /**
     * 创建一个 kafka 的 consumer source
     *
     * @param eventClass --实体对象
     * @param <T>        --实体对象泛型类型
     * @return
     */
    @JSONField(serialize = false)
    public <T> FlinkKafkaConsumer011<T> getKafkaConsumer(Class<T> eventClass) {
        Pattern pattern = Pattern.compile(this.topic);
        if (pattern.matcher(pattern.pattern()).matches()) {
            //使用 单topic 或 多topic
            return new FlinkKafkaConsumer011<T>(
                    Arrays.stream(this.topic.split(",")).map(String::trim).collect(Collectors.toList()),
                    getKafkaEventSchema(eventClass),
                    this.getProperties());
        }

        //使用 Topic 发现
        return new FlinkKafkaConsumer011<T>(
                pattern,
                getKafkaEventSchema(eventClass),
                this.getProperties());

    }
}