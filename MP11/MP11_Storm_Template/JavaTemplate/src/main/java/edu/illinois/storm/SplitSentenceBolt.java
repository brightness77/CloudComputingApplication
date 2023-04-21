package edu.illinois.storm;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;


/** a bolt that split sentences */
public class SplitSentenceBolt extends BaseBasicBolt {

    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector) {
        /* ----------------------TODO-----------------------
        Task: split sentence and emit words
            Hint: split on "[^a-zA-Z0-9-]"
        ------------------------------------------------- */
        String[] parts = tuple
                .getStringByField("sentence")
                .split("[^a-zA-Z0-9-]");

        for(String part: parts){
            if(!part.isEmpty()){
                collector.emit(new Values(part));
            }
        }

        // End
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    /* ----------------------TODO-----------------------
    Task: declare output fields
    ------------------------------------------------- */
        declarer.declare(new Fields("word"));
        // End
    }
}
