package hust.cs.javacourse.search.index.impl;

import hust.cs.javacourse.search.index.AbstractDocumentBuilder;
import hust.cs.javacourse.search.index.AbstractIndex;
import hust.cs.javacourse.search.index.AbstractIndexBuilder;
import hust.cs.javacourse.search.util.FileUtil;

import java.io.File;
import java.util.List;

public class IndexBuilder extends AbstractIndexBuilder {
    public IndexBuilder(AbstractDocumentBuilder docBuilder) {
        super(docBuilder);
    }

    /**
     * <pre>
     * 构建指定目录下的所有文本文件的倒排索引
     * @param rootDirectory ：指定目录
     * @return ：构建好的索引
     * </pre>
     */
    public AbstractIndex buildIndex(String rootDirectory) {
        AbstractIndex index = new Index();
        List<String> docPaths = FileUtil.list(rootDirectory, ".txt");
        int docId = 0;
        for (String i : docPaths) {
            DocumentBuilder builder = new DocumentBuilder();
            index.addDocument(builder.build(docId++, i, new File(i)));
        }
        //index.save(new File(rootDirectory + "/index.dat"));
        return index;
    }
}
