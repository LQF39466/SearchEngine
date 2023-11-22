package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocument;
import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractTermTuple;
import hust.cs.javacourse.search.parse.AbstractTermTupleStream;
import hust.cs.javacourse.search.parse.impl.LengthTermTupleFilter;
import hust.cs.javacourse.search.parse.impl.PatternTermTupleFilter;
import hust.cs.javacourse.search.parse.impl.StopWordTermTupleFilter;
import hust.cs.javacourse.search.parse.impl.TermTupleScanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;


public class DocumentBuilder extends AbstractDocumentBuilder {
    /**
     * <pre>
     * 由解析文本文档得到的TermTupleStream,构造Document对象.
     * @param docId             : 文档id
     * @param docPath           : 文档绝对路径
     * @param termTupleStream   : 文档对应的TermTupleStream
     * @return ：Document对象
     * </pre>
     */
    public AbstractDocument build(int docId, String docPath, AbstractTermTupleStream termTupleStream) {
        Document doc = new Document(docId, docPath, new ArrayList<>());
        AbstractTermTuple tmp = termTupleStream.next();
        while (tmp != null) {
            doc.addTuple(tmp);
            tmp = termTupleStream.next();
        }
        return doc;
    }

    /**
     * <pre>
     * 由给定的File,构造Document对象.
     * @param docId     : 文档id
     * @param docPath   : 文档绝对路径
     * @param file      : 文档对应File对象
     * @return          : Document对象
     * </pre>
     */
    public AbstractDocument build(int docId, String docPath, File file) {
        try {
            AbstractTermTupleStream termTupleStream = new TermTupleScanner(new BufferedReader(new FileReader(file)));
            return build(docId, docPath, new StopWordTermTupleFilter(new PatternTermTupleFilter(new LengthTermTupleFilter(termTupleStream))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
