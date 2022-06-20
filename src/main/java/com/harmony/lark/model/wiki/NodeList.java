package com.harmony.lark.model.wiki;

import com.harmony.lark.Node;
import com.harmony.lark.model.ListResult;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class NodeList implements ListResult<Node> {

    @Override
    public boolean getHasMore() {
        return true;
    }

    @NotNull
    @Override
    public String getPageToken() {
        return "";
    }

    @Override
    public int getTotal() {
        return 1;
    }

    @NotNull
    @Override
    public List<com.harmony.lark.Node> getItems() {
        return Collections.emptyList();
    }

    @Data
    public static class Node {

        private Boolean hasChild;

        private String title;

    }

}
