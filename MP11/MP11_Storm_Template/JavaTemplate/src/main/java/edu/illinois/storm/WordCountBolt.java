package edu.illinois.storm;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;

/** a bolt that tracks word count */
public class WordCountBolt extends BaseBasicBolt {
    // Hint: Add necessary instance variables if needed
    private OutputCollector collector;
    private Map<String, Integer> countMap = new HashMap<>();


    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        /* ----------------------TODO-----------------------
        Task: word count
		Hint: using instance variable to tracking the word count
        ------------------------------------------------- */

        String word = tuple.getStringByField("word");
        Integer count = countMap.get(word);

        if(count == null){
            count = 0;
        }
        ++count;

        countMap.put(word, count);
        collector.emit(new Values(word, count));
        // End
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    /* ----------------------TODO-----------------------
    Task: declare output fields
    ------------------------------------------------- */
        declarer.declare(new Fields("word", "count"));
        // End
    }
}
