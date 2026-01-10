public class Business {

  private String name;
  private String id;
  private int glassesQty;
  private int beltQty;
  private int scarfQty;

  public Business(String name, String id) {
    this.name = name;
    this.id = id;
    this.glassesQty = 0;
    this.beltQty = 0;
    this.scarfQty = 0;
  }

  public String getName() {
    return name;
  }

  public String getId() {
    return id;
  }

  public void addQuantity(int itemType, int quantity) {
    switch (itemType) {
      case 1:
        glassesQty += quantity;
        break;
      case 2:
        beltQty += quantity;
        break;
      case 3:
        scarfQty += quantity;
        break;
    }
  }

  @Override
  public String toString() {
    return (
      "Business: " +
      name +
      " (ID: " +
      id +
      ") | Inventory: G=" +
      glassesQty +
      ", B=" +
      beltQty +
      ", S=" +
      scarfQty
    );
  }
}
