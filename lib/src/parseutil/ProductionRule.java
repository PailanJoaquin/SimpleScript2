package lib.src.parseutil;

import java.util.List;

public class ProductionRule {
    private final ItemType lhs;
    private final List<ItemType> rhs;

    public ProductionRule(ItemType lhs, List<ItemType> rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public ItemType getLHS() {
        return lhs;
    }

    public List<ItemType> getRHS() {
        return rhs;
    }

    @Override
    public String toString() {
        return lhs + " → " + rhs;
    }
}
