package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.StopWords;

import java.util.Arrays;
import java.util.List;

public class StopWordTermTupleFilter extends AbstractTermTupleFilter {
    /**
     * 构造函数
     *
     * @param input ：Filter的输入，类型为AbstractTermTupleStream
     */
    public StopWordTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
    }

    /**
     * 获得下一个三元组
     *
     * @return 下一个三元组；按停用词过滤，如果到了流的末尾，返回null
     */
    public AbstractTermTuple next() {
        AbstractTermTuple tmp;
        while (true) {
            tmp = input.next();
            if (tmp == null) return null;
            List<String> stopWords = Arrays.asList(StopWords.STOP_WORDS);
            if (!stopWords.contains(tmp.term.getContent())) break;
        }
        return tmp;
    }
}
