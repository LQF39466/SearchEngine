package hust.cs.javacourse.search.run;

import hust.cs.javacourse.search.index.impl.Term;
import hust.cs.javacourse.search.query.AbstractHit;
import hust.cs.javacourse.search.query.AbstractIndexSearcher;
import hust.cs.javacourse.search.query.Sort;
import hust.cs.javacourse.search.query.impl.IndexSearcher;
import hust.cs.javacourse.search.query.impl.SimpleSorter;
import hust.cs.javacourse.search.util.Config;

import java.util.Scanner;

/**
 * 测试搜索
 */
public class TestSearchIndex {
    public static void main(String[] args) {
        Sort simpleSorter = new SimpleSorter();
        String indexFile = Config.INDEX_DIR + "index.dat";
        AbstractIndexSearcher searcher = new IndexSearcher();
        searcher.open(indexFile);
        while (true) {
            System.out.println("Please select a fuction you wish to test");
            System.out.println("1. search by word");
            System.out.println("2. search by two words with AND logic");
            System.out.println("3. search by two words with OR logic");
            System.out.println("4. search by phrase");
            System.out.println("0. exit");
            Scanner input = new Scanner(System.in);
            AbstractHit[] hits;
            switch (input.nextInt()) {
                case 0:
                    return;
                case 1:
                    System.out.println("Input a word you wish to search: ");
                    Scanner input1 = new Scanner(System.in);
                    String tmp1 = input1.nextLine();
                    hits = searcher.search(new Term(tmp1), simpleSorter);
                    break;
                case 2:
                    System.out.println("Input two words you wish to search: ");
                    Scanner input2 = new Scanner(System.in);
                    String tmp2 = input2.nextLine();
                    String tmp3 = input2.nextLine();
                    hits = searcher.search(new Term(tmp2), new Term(tmp3), simpleSorter, AbstractIndexSearcher.LogicalCombination.AND);
                    break;
                case 3:
                    System.out.println("Input two words you wish to search: ");
                    Scanner input3 = new Scanner(System.in);
                    String tmp4 = input3.nextLine();
                    String tmp5 = input3.nextLine();
                    hits = searcher.search(new Term(tmp4), new Term(tmp5), simpleSorter, AbstractIndexSearcher.LogicalCombination.OR);
                    break;
                case 4:
                    System.out.println("Input a phrase you wish to search: ");
                    Scanner input4 = new Scanner(System.in);
                    String tmp6 = input4.nextLine();
                    String tmp7 = input4.nextLine();
                    IndexSearcher phraseSearcher = new IndexSearcher();
                    phraseSearcher.open(indexFile);
                    hits = phraseSearcher.search(new Term(tmp6), new Term(tmp7), simpleSorter);
                    break;
                default:
                    continue;
            }
            if (hits == null || hits.length == 0) {
                System.out.println("Not Found!");
                continue;
            }
            for (AbstractHit hit : hits) {
                System.out.println(hit);
                System.out.println("--------------------");
            }
        }
    }
}