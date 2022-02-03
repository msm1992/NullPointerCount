package org.example.hadoop.mapreduce.impl;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class NullPointerCount {

    public static class MapperImpl extends Mapper<LongWritable, Text, Text, IntWritable> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();

            if(line.contains("NullPointerException")) {
                context.write(new Text("NullPointerException"), new IntWritable(1));
            }
        }
    }

    public class ReducerImpl extends Reducer<Text, IntWritable, Text, IntWritable> {
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException,
                InterruptedException {
            int count = 0;
            for (IntWritable n : values) {
                count += n.get();
            }
            context.write(key, new IntWritable(count));
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "NullPointerCount");

        job.setJarByClass(NullPointerCount.class);
        job.setMapperClass(MapperImpl.class);
        job.setReducerClass(ReducerImpl.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        Path outputPath = new Path(args[1]);
        outputPath.getFileSystem(conf).delete(outputPath, true);

        System.exit(job.waitForCompletion(true) ? 0 : 1);

    }
}
