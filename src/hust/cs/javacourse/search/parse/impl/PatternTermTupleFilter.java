package hust.cs.javacourse.search.parse.impl;

import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleFilter;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.util.Config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTermTupleFilter extends AbstractTermTupleFilter {
    /**
     * 构造函数
     *
     * @param input ：Filter的输入，类型为AbstractTermTupleStream
     */
    public PatternTermTupleFilter(AbstractTermTupleStream input) {
        super(input);
    }

    /**
     * 获得下一个三元组
     *
     * @return 下一个三元组；按正则表达式过滤，如果到了流的末尾，返回null
     */
    public AbstractTermTuple next() {
        AbstractTermTuple tmp;
        Pattern p = Pattern.compile(Config.TERM_FILTER_PATTERN);
        while (true) {
            tmp = input.next();
            if (tmp == null) return null;
            Matcher m = p.matcher(tmp.term.getContent());
            if (m.matches()) break;
        }
        return tmp;
    }
}

