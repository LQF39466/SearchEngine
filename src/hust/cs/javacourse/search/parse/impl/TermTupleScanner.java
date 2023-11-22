package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.index.impl.TermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleScanner;
import hust.cs.javacourse.search.util.Config;
import hust.cs.javacourse.search.util.StringSplitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TermTupleScanner extends AbstractTermTupleScanner {
    public TermTupleScanner() {
    }

    public TermTupleScanner(BufferedReader input) {
        super(input);
    }

    protected List<String> buf = new ArrayList<>();
    protected int curPos = 0;

    /**
     * 获得下一个三元组
     *
     * @return 下一个三元组；如果到了流的末尾，返回null
     */
    public AbstractTermTuple next() {
        try {
            while (buf.isEmpty()) {
                String tmp;
                do {
                    tmp = input.readLine();
                    if (tmp == null) return null;
                } while (tmp.isEmpty());
                StringSplitter splitter = new StringSplitter();
                splitter.setSplitRegex(Config.STRING_SPLITTER_REGEX);
                buf = splitter.splitByRegex(tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        AbstractTermTuple tuple = new TermTuple();
        tuple.term = new Term();
        if (Config.IGNORE_CASE) tuple.term.setContent(buf.get(0).toLowerCase());
        else tuple.term.setContent(buf.get(0));
        buf.remove(0);
        tuple.curPos = this.curPos++;
        return tuple;
    }
}
