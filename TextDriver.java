import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/* 
Bu classın amacı kullanıcının hazırladığı actions dosyasını okuyup, kullanıcının verdiği komutlarla ilişkili methodları çalıştırıp texti
düzenlemektir.

Bu classda sadece main methodu kullandım başka herhangi bir method yok.

Varsaydığım durumlar ve hatalı işlemler hakkında: https://prnt.sc/znfSaLRKY5Ro

*/
public class TextDriver {

    //main method
    public static void main(String[] args) {
        TextEditor texteditor = new TextEditor();// Text Editör objesi initialize eder. Yapacağımız tüm işlemler bu objenin üstünde olacak.
        File myObj = new File("actions.txt");// File classının constructor'ına actions dosyasını girer ve bi File objesi oluşturur.
        //** ÖNEMLİ Alttaki değişkeni burada oluşturdum ki exception catchlerken şu satırı değiştir gibi bir şey yazdırabileyim.
        //** Diğer türlü catch bloğunun içinde bu değişkeni göremiyordu.
        String data = "";// Kullanıcının dosyasındaki her satırı teker teker tutacak olan değişkeni oluşturur ve initialize eder.
        try{// bu try bloğu içindeki tümm kodları çalıştırıp herhangi bir exception fırlatılırsa catch bloğunda yakalar ve uygun mesaj verilir.
            Scanner myReader = new Scanner(myObj);// Scanner objesi oluşturup constuctorına oluşturduğumu file objesini girer.
            while (myReader.hasNextLine()) { // Scanner objesinin yani kullanıcıdan aldığımız dosyanın bi sonraki satırı olmayıncaya kadar;
                data = myReader.nextLine();// Bir sonraki satıra geçer.
                //** Aşağıdaki kod satırını StackOverflow sitesinden buldum. Neden böyle yaptığımı da deftere çizip ekran görüntüsünün linkini ekledim.
                //satırdaki boşlukları yok ediyor replace methoduyla(String classının) ilk halinden çıkartıyor ve boşluk sayısını bulmuş oluyor.
                int space_count = data.length() - data.replace(" ", "").length();
                String[] array = data.split(" ");//Kullanıcının girdiği satırı boşluklarından ayırarak String arrayinin içine atar.
                String text="";// Sadece, kullanıcının eklemek istediği texti tutacak olan değişkeni initialize eder.

                //**Kullanıcının girdiği satırın ilk kısmı(action type)(equalsIgnoreCase methoduyla büyük kücük karakter farketmiyor.)
                if(array[0].equalsIgnoreCase("insert")){//insert girildiyse;
                    for(int i=1;i<space_count;i++){// Alttaki 3 satırı deftere çizip anlattım -> https://prnt.sc/vmU_eRWDWnqt
                        text+=array[i];
                        if(i!=space_count-1) text+=" ";
                    }
                    //texteditor objesi ile insert methodunu kullanır. Parametre olarak kullanıcının eklemek istediği texti ve
                    // eklemek istediği position'ı girer (position string olarak alındıgı için parseint methoduyla int e cevirir)
                    texteditor.insert(text,Integer.parseInt(array[space_count]));
                    // Kullanıcıya bilgi vermek için yapılan işlem ve detaylarını yazdırır.
                    System.out.println("Inserted: '"+ text+"' Between index "+ Integer.parseInt(array[space_count])+ " and "+(Integer.parseInt(array[space_count])+text.length()-1));
                    //bi işlem yapıldığı için ve her işlemden sonra indexleri kolayca görmek için teker teker elemanları yazdırır.
                    texteditor.printText();
                }else if(array[0].equalsIgnoreCase("delete")){//* delete girildiyse;
                    if(Integer.parseInt(array[2])!=0){//split methoduyla böldüğümüz textin 3. kısmı yani length'i 0 değilse;
                        //delete methodununa satırın 2. ve 3. yawni position ve length'i girer. Silinen texti kullanıcıya vermek için tutar.
                        String oldText = texteditor.delete(Integer.parseInt(array[1]), Integer.parseInt(array[2]));
                        // Kullanıcıya bilgi vermek için yapılan işlem ve detaylarını yazdırır.
                        System.out.println("Deleted: '"+ oldText+"' Between index "+ Integer.parseInt(array[1])+ " and "+(Integer.parseInt(array[1])+Integer.parseInt(array[2])-1));
                    }else{// silinmek istenen length 0 sa hiçbir şey silinmeyecektir.
                        System.out.println("Nothing has been deleted.");// kullanıcıya bunun bilgisini verir.
                    }
                    //bi işlem yapıldığı için ve her işlemden sonra indexleri kolayca görmek için teker teker elemanları yazdırır.
                    texteditor.printText();
                }else if(array[0].equalsIgnoreCase("replace")){//* replace girildiyse;
                    for(int i=1;i<space_count-1;i++){// Bu da insert'deki deftere çizdiğim mantıkla aynı sadece fazladan 1 length giriliyor.
                        text+=array[i]; //https://prnt.sc/vmU_eRWDWnqt
                        if(i!=space_count-2) text+=" ";
                    }
                    //replace methodununa eklemek istenilen texti, position ve lenghti girer.
                    texteditor.replace(text, Integer.parseInt(array[space_count-1]), Integer.parseInt(array[space_count]));
                    // Kullanıcıya bilgi vermek için yapılan işlem ve detaylarını yazdırır.
                    System.out.println("Replaced: " + text + ", Between index " + Integer.parseInt(array[space_count-1]) + " and " + (Integer.parseInt(array[space_count-1])+Integer.parseInt(array[space_count])-1));
                    //bi işlem yapıldığı için ve her işlemden sonra indexleri kolayca görmek için teker teker elemanları yazdırır.
                    texteditor.printText();
                }else if(array[0].equalsIgnoreCase("undo")){// undo ise;
                    texteditor.undo();// undo methodunu çalıştırır.
                }else if(array[0].equalsIgnoreCase("redo")){// redo ise;
                    texteditor.redo(); // redo methodunu çalıştırır.
                }else{// eğer satırın ilk kısmı(actin type) hiçbir action ile eşleşmezse yanlış girilmiş demektir;
                    System.out.println("Invalid type of action: " + array[0]);//ve bunun bilgisini verir.
                    break;// döngüyü kırar(programı sonlandırır).
                }
            }
        }catch (TextEditor.ArraySizeExceeded e) {// eğer yapılan işlemlerin kısmı arrayin sınırını aştıysa fırlatılan Exceptionı yakalar;
        System.out.println(e.getMessage()+ "\nPlease enter appropriate values -> " + data);//Uygun hata mesajını yazdırır.
        }catch (FileNotFoundException e) {// Eğer verilen dosya adıyla ilgili dosyayı bulamıyorsa fırlatılan Exceptionı yakalar;
        System.out.println("File not found!");//Uygun hata mesajını yazdırır. 
        }catch (IndexOutOfBoundsException e) {// Eğer position veya length'e negatif bir sayı girilirse fırlatılan Exceptionı yakalar;
        System.out.println("Position and length must be greater than or equal to 0! \nCorrect this line -> " + data);//Uygun hata mesajını yazdırır.
        }catch (NumberFormatException e) {// Eğer position veya length'e girilen değer int değilse fırlatılan Exceptionı yakalar;
        System.out.println("Position and length must be integer types! \nCorrect this line -> " + data);//Uygun hata mesajını yazdırır.
        }
        //Yapılan işlemler hatalı olsun veya olmasın her türlü;
        System.out.println("Final Text: "+texteditor.toString());// Textin son halini yazdırır.
    }
}
