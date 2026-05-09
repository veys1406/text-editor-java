/*
Bu classın amacı yaptığımız her işlemi(insert,delete ve replace) tüm detaylarıyla (işlemin türü, işlemin yapıldığı pozisyon, eklenen
text, silinen text) tutmaktır çünkü bu classın objeleri yerine göre undo ve redo stack'inin içinde tutulacak. Kullanıcı istediğinde
bu objeler poplanıp tutulan bilgilerle geri alma veya ileri alma işlemleri yapılacak.

Bu class instance variable olarak String türünde işlemin türünü temsil eden type, String türünde eklediğimiz texti temsil eden newText,
String türünde sildiğimiz veya replace ettiğimiz texti temsil eden oldText ve int türünde bu işlemlerin yapıldığı position'u tutar.

Oluşturduğum methodlar ve amaçları;
    -Action(): (Default constructor) action objelerini içine değer girmeden initialize etmek için oluşturdum.
    -Action(String type, String newText, String oldText, int position): (Parametreli Constructor) yapılan işlemlerin detaylarını
        parametre olarak alır ve değişkenlerini bunlara göre düzenler.
    getType(): type dışarıdan değiştirilmesin diye private olduğu için objenin typeını return eder.
    getNewText(): newText dışarıdan değiştirilmesin diye private olduğu için objenin newText'ini return eder.
    getOldText(): oldText dışarıdan değiştirilmesin diye private olduğu için objenin oldText'ini return eder.
    getPosition(): position dışarıdan değiştirilmesin diye private olduğu için objenin position'ını return eder.

Setter methodları oluşturmaya gerek duymadım çünkü objenin değişkenlerini hiçbir zaman değiştirmicem.
Bu class bana sadece bilgi vermek için var.
*/

public class Action {
    //Dışarıdan erişilmemesi için değişkenlerimin hepsini private yaptım.
    private String type;   //String türünde işlemin türünü temsil eden type değişkeni. 
    private String newText;//String türünde eklediğimiz texti temsil eden newText.
    private String oldText;//String türünde sildiğimiz veya replace ettiğimiz texti temsil eden oldText
    private int position;  //int türünde bu işlemlerin yapıldığı position.

    //Default Constructor
    public Action(){}// Herhangi bir şey yapmasına gerek yok sadece objeyi oluştursun yeter.

    //Parametreli Constructor
    // Parametre olarak yapılan işlemin detaylarını alır, değişkenleri buna göre değiştirir.
    public Action(String type, String newText, String oldText, int position) {
        this.type = type;// objenin type değişkenini girilen type olarak değiştirir.
        this.newText = newText;// objenin newText değişkenini girilen newText olarak değiştirir.
        this.oldText = oldText;// objenin oldText değişkenini girilen oldText olarak değiştirir.
        this.position = position;// objenin position değişkenini girilen position olarak değiştirir.
    }

    //Accessor methods:

    //Objenin type değişkenini return eder.
    public String getType() {
        return type;
    }

    //Objenin newText değişkenini return eder.
    public String getNewText() {
        return newText;
    }

    //Objenin oldText değişkenini return eder.
    public String getOldText() {
        return oldText;
    }

    //Objenin position değişkenini return eder.
    public int getPosition() {
        return position;
    }

}
