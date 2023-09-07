import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IndexAggregation {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);
        // 创建一个DataStream，用于接收输入数据
        DataStream<int[]> inputDataStream = env.fromElements(
                new int[]{1, 10},
                new int[]{2, 30},
                new int[]{1, 5},
                new int[]{3, 10},
                new int[]{2, 15},
                new int[]{1, 1}
        );
        // 使用Flink的KeyedStream进行按索引分区
        DataStream<List<Integer>> aggregatedDataStream = inputDataStream
                .keyBy(data -> data[0])
                .window(EventTimeSessionWindows.withGap(Time.seconds(1)))
                .sum(1).map((MapFunction<int[], List<Integer>>) ints -> Arrays.stream(ints)
                        .boxed().collect(Collectors.toList()));
        // 打印输出结果
        aggregatedDataStream.print();
        env.execute("Index Aggregation Example");
    }
}

