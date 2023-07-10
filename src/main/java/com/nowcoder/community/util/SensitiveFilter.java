package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Szw 2001
 * @Date 2023/6/12 13:50
 * @Slogn 致未来的你！
 */
@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACE_MARK="***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    //@PostConstruct该注解被用来修饰一个非静态的void（）方法。
    //被@PostConstruct修饰的方法会在服务器加载Servlet的时候运行，并且只会被服务器执行一次。
    //这两个注解可用于修饰两个非静态的void方法，而且这个方法不能抛出异常声明。
    @PostConstruct
    public void init(){
        try(
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
        ){
        String keyWord;
        while ((keyWord = bufferedReader.readLine())!=null){
            //添加到前缀树中
            this.addKeyWord(keyWord);
        }
        }catch (IOException e){
        logger.error("加载敏感词 文件失败"+e.getMessage());
        }

    }

    //将一个敏感词添加到前缀树中
    private void addKeyWord(String keyWord) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyWord.length(); i++) {
            char c = keyWord.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode==null){
                //初始话子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            tempNode = subNode;

            if (i == keyWord.length() - 1){
                tempNode.setIeKeyWordEnd(true);
            }
        }
    }

    //判断是否为符号
    private boolean isSymbol(Character c){
        //0x2E80~0x9FFF东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80 || c > 0x9FFF);
    }

    /**
     * 过滤敏感词
     **/
    public String filter(String text){
        if (StringUtils.isBlank(text)){
            return null;
        }

        //指针1
        TrieNode tempNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();
        while (position < text.length()){
            char c = text.charAt(position);
            //跳过符号
            if (isSymbol(c)){
                //若指针处于根节点,将此符号计入结果,让指针2向下走一步
                if (tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头或中间,指针3都向下走一步
                position++;
                continue;
            }

            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null ){
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = rootNode;
            }else if (tempNode.isIeKeyWordEnd()){
                sb.append(REPLACE_MARK);
                begin = ++position;
                tempNode = rootNode;
            }else {
                position++;
            }
        }
        sb.append(text.substring(begin));

        return sb.toString();

    }

    //前缀树
    private class TrieNode{

        //关键词结束标识
        private boolean ieKeyWordEnd = false;

        //子节点（Key是下级字符,value是下级节点）
        private Map<Character,TrieNode> subNodeMap = new HashMap<>();

        public boolean isIeKeyWordEnd() {
            return ieKeyWordEnd;
        }

        public void setIeKeyWordEnd(boolean ieKeyWordEnd) {
            this.ieKeyWordEnd = ieKeyWordEnd;
        }

        //添加子节点
        public void addSubNode(Character character,TrieNode trieNode){
            subNodeMap.put(character,trieNode);
        }

        //获取子节点方法
        public TrieNode getSubNode(Character character){
            if (!subNodeMap.containsKey(character)){
                return null;
            }else {
                return subNodeMap.get(character);
            }
        }
    }
}
