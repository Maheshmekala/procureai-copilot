public class Supplier {
    private int id;
    private String name;
    private String category;
    private double annualSpend;

    public Supplier() {}

    public Supplier(int id, String name, String category, double annualSpend) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.annualSpend = annualSpend;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getAnnualSpend() { return annualSpend; }
    public void setAnnualSpend(double annualSpend) { this.annualSpend = annualSpend; }

    @Override
    public String toString() {
        return id + " | " + name + " | " + category + " | $" + annualSpend;
    }

    public void display() {
        System.out.println(this);
    }
}