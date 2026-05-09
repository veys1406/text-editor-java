import java.util.ArrayList; 
// Java'nın kendi ArrayList classını kullandım.
// LinkedStack, Stack, SinglyLinkedList için Goodrich'in dosyalarını kullandım. Herhangi bir şey değiştirmedim bu dosyalarda.
/*
Bu classın amacı kullanıcıdan aldığımız dosyadaki komutlara gore kullandığımız methodların ne yaptığını belirlemek ve TextEditor
objesi oluşturmaktır.

Bu class instance variable olarak yaptığımız actionları saklamak ve sonra undo ve redo methodlarında kullanmak 2 tane stack
ve textin asıl tutulacağı yer olan arraylisti tutar.Stacklerin içinde Action objelerini tutar ki her işlemin detaylarını 
saklayıp sonra kullanabilelim. Text'i tutmak için ArrayList kullandım add, set, remove methodlarının işimi kolaylaştıracağını 
düşündüm ve tür olarak da String'in Character'den daha uygun olacağını düşündüm insert methodunda split yaptıktan sonra char'a
dönüştürmemek için.

Oluşturduğum methodlar ve amaçları;
    -Default Constructor(): Undo, redo stackini ve texti tuttuğumuz arraylisti initialize eder. Undo ve redo stackleri için LinkedStack
        kullandım ama ArrayStack de kullanabilirdim ve hiçbir şey değişmezdi. Texti de neden ArrayList olarak tuttum yukarda anlattım.
    -insert(String text, int position): Kullanıcının istediği indexten başlayarak yine kullanıcıdan aldığımız texti her bir
        karakterine ayırarak teker teker arrayliste ekler. Girilen değerlerle bi Action classı oluşturup undoStack'e pushlar ki
        sonrasında bu işlemi geri alabilelim. En sonunda herhangi bir işlemden sonra redo kullanılmasın diye redoStack'i
        temizler tamamen. Herhangi bir şey return etmez.
    -delete(int position, int length): Yine kullanıcının istediği indexten başlayarak girilen length değeri kadar elemanı siler ve
        sağındaki elemanları kaydırır. Insert methodunda da yaptığı gibi Action objesi oluşturup bunu undoStack'ine pushlar ve redo
        yapılmasın bu işlemden sonra diye redoStack'ini clearlar. En sonda silinen texti return eder ki kullanıcıya silinen texti 
        yazdırabileyim.
    -replace(String newText, int position, int length): Kullanıcının istediği pozisyondan başlayıp length uzunluğunda karakteri
        siler ama bu silme işlemi delete methodundaki gibi değildir sadece o elemanları null yapar herhangi bir remove işlemi yoktur.
        Ardından kullanıcının verdiği texti yine verdiği pozisyondan başlayarak ekler. Girilen değerlerle bi Action classı oluşturup
        undoStack'e pushlar ki sonrasında bu işlemi geri alabilelim ve redo yapılmasın bu işlemden sonra diye redoStack'ini clearlar.
        Herhangi bir şey return etmez.
    -undo(): undoStack'i boş değilse en son yapılan işlemi oluşturduğumuz action objesi sayesinde geri alır o işlemin türüne göre:
        son işlem insert ise -> insert yapılan pozisyondan başlayarak insert yapılan text uzunluğu kadar elemanı siler(delete methoduyla).
        son işlem delete ise -> silinen pozisyondan başlayarak silinen texti ekler (insert methoduyla).
        son işlem replace ise -> replace edilen yeni texti siler yerine eski texti ekler(replace methoduyla).
            (replace methodunda newtext yerine action objesinde tuttuğumuz oldText,position,oldText'in uzunluğu(length'i tutmuyoruz çünkü))
        //***ONEMLI*** 
            Üstteki her koşuldan sonra methodun daima yaptığı 2 tane şey vardır;
                -undoStack'ini pop eder çünkü insert,delete ve replace methodlarını kullandığımız için fazladan bi action objesi
                    pushlanır undoStack'ine.
                -actionDone adında yaptığımız undo işlemini redo yaparken kolaylık sağlasın diye saklayan bi action objesi
                    oluşturur ve en sonda bu action objesini redoStack'e pushlar.
        *** ***
    -redo(): redoStack'i boş değilse son yapılan undo işlemini geri alır. Undo methodundan farkı çok çok az eğer undo methoduna
        parametre olarak bi stack alsaydık redo methodunun içinde undo methodunu kullanarak aynı şeyi yapabilirdik bu.
    -clear(): yapılan her insert,delete ve replace methodundan sonra redo yapılmasın diye redoStack'ini clearlar.
    -toString(): Programın en sonunda final texti yazdırmak için yazdım bu methodu arraylistin tüm elemanlarını bi stringe atıp return eder.
    -printText(): Texti tuttuğumuz arraylistin her bir elemanını ayrı ayrı [] şeklinde yazdırır.(indexleri gormek kolay olur diye yazdım).

    Kendi oluşturduğum Exception: ArraySizeExceeded

    */

public class TextEditor {
    private Stack<Action> undoStack;// Yapılan işlemlerin geri alınabilmesi için action objesini tutan stack
    private Stack<Action> redoStack;// Yapılan geri alma işlemininn geri alınabilmesi için action objesini tutan stack
    private ArrayList<String> currentText;// Asıl texti tutan Arraylist. Her bir elemanı textimizin bir karakterini tutar.
    // (Aslında Character türünde oluşturacaktım ama split methodu String arrayi oluşturduğu ve bunları chara dönüştürmemek için
    // String olarak oluşturdum bu arraylisti)

    public TextEditor() {// TextEditor objesi oluşturmak için default constructor. Maini kullanıcıdan bağımsız yazdığımız için
                         // copy constructor veya parametreli constructor yazma gereği duymadım 1 kere kullanıyorum sonuçta.
        undoStack = new LinkedStack<>();// yapılan işlemlerin action objelerini tutacak undoStack'i initialize eder.
        redoStack = new LinkedStack<>();// yapılan işlemlerin action objelerini tutacak redoStack'i initialize eder.
        currentText = new ArrayList<>();// Texti tutan arraylisti initialize eder.
    }

    //parametre olarak String türünde text ve int türünde position alır eğer arrayin sınırı aşılırsa kendi eklediğim ArraySizeExceeded
    //Exception'u atar. Herhangi bir şey return etmez.
    public void insert(String text, int position)throws ArraySizeExceeded{
        String[] array = text.split("");// Parametre olarak alınan texti split methoduyla her bir karaktere ayırıp
                                              // String arrayinin içine atar.
        int cursor = position;// Parametre olarak alının position değişkenini arttırmak istemedim çünkü action objesini oluştururken
                              // lazım olacak onun yerine cursor değişkenini oluşturup bunu arttırıcam.
        if(currentText.size()>=cursor){ // Eğer position, arrayin sınırlarını aşmıyorsa ekleme işlemini yapar.
            for(String x : array){ // Karakter karakter ayırdığımız textin tüm elemanlarını dolaşan for-each dongusu.
                currentText.add(cursor,x);// ArrayListin add methoduyla cursor'ıncı indexe split ettiğimiz textin karakterini ekler.
                cursor++;// Her işlemden sonra cursorı arttırır ki ekleye ekleye ilerlesin.
            }
            // Action classının constructorını kullanarak yeni bi action objesi oluşturur. Parametreye, action type olarak inserti,
            // newText oalrak alınan text i, old texti olarak null ( silinen bi karakter yok ), ve alının position değişkenini alır
            undoStack.push(new Action("insert",text,null,position));
        }else{ // Eğer position, arrayin sınırlarını aşıyorsa;
            throw new ArraySizeExceeded("Text boundary exceeded."); // Kendi oluşturduğum ArraySizeExceeded Exception'ını atar.
                                                // ve mainde mesajı yazdırmak için constructorın parametresi olarak mesaj girer
        }
        clear();// insert işlemi yapıldıktan sonra redo yapılmasın diye redoStack'ini clearlar.
    } 

    // Parametre olarak int türünde position ve length alır position indexinden başlayıp length uzunluğunda karakteri siler(remove)
    // Eğer arrayin sınırı aşılırsa kendi eklediğim ArraySizeExceeded Exception'u atar. Silinen karakteri return eder komple.
    public String delete(int position, int length)throws ArraySizeExceeded{
        String oldText = "";// Action objesini oluştururken oldText(silinen text)'i tutması için String türünde değişken initialize eder.
        if (length + position <= currentText.size()) {// İşlem yapılacak kısımın hepsi arraylistin sınırları içinde kalıyorsa;
            for(int i=0; i<length;i++){// parametrede alının length kadar döner 
                oldText += currentText.get(position);// ilk başta return edeceğimiz oldtexte sildiğimiz karakterleri ekler. 
                currentText.remove(position);// sonrasında arraylistin positionuncu indexindeki elemanı siler.
            } // ***ONEMLI*** her remove işleminden son sağdaki karakterler shiftleneceği için positionı arttırmamıza gerek yok.

            // Action classının constructorını kullanarak yeni bi action objesi oluşturur. Parametreye, action type olarak deletei,
            // newText olarak null(bir şey eklemedik), old texti olarak silinen karakteri, ve alının position değişkenini alır.
            undoStack.push(new Action("delete",null,oldText,position));
            clear();// delete işlemi yapıldıktan sonra redo yapılmasın diye redoStack'ini clearlar.
            return oldText; //sildiğimiz karakterleri tuttuğumuz oldtext stringini return eder
        }else{// İşlem yapılacak kısımın hepsi arraylistin sınırlarını aşıyorsa;
            throw new ArraySizeExceeded("Text boundary exceeded.");// Kendi oluşturduğum ArraySizeExceeded Exception'ını atar.
                                                // ve mainde mesajı yazdırmak için constructorın parametresi olarak mesaj girer
        }
    }
    
    //  Parametre olarak String türünde text ve int türünde position ve length alır. İlk önce positiondan başlayarak length 
    // uzunluğu kadar karakteri siler(remove değil null yapar o karakteri). Ardından positiondan başlayarak girilen stringi ekler
    // (add değil,set methoduyla. Kaydırmaz sağındaki elemanları) Eğer arrayin sınırı aşılırsa da kendi eklediğim ArraySizeExceeded 
    // Exception'u atar. Herhangi bir şey return etmez.
    public void replace(String newText, int position, int length)throws ArraySizeExceeded{
        String oldText = "";// Action objesini oluştururken oldText(silinen text)'i tutması için String türünde değişken initialize eder.
        String[] array = newText.split("");// Parametre olarak alınan texti split methoduyla her bir karaktere ayırıp
                                                 // String arrayinin içine atar.
        if((currentText.size()) >= (position+length)){// İşlem yapılacak kısımın hepsi arraylistin sınırları içinde kalıyorsa;
            int cursor = position;// Parametre olarak alının position değişkenini arttırmak istemedim çünkü action objesini oluştururken
                              // lazım olacak onun yerine cursor değişkenini oluşturup bunu arttırıcam.
            for(int i=0;i<length;i++){// parametrede alının length kadar döner 
                if(currentText.get(cursor)!=null) // Arraylistin cursorıncı elemanı null değilse;
                    oldText += currentText.get(cursor); // action objesine ekleyeceğimiz oldtexte sildiğimiz karakterleri ekler. 
                else // Arraylistin cursorıncı elemanı null ise;
                    oldText += " ";// boş bi karakter ekler
                if(i<array.length) // parametre olarak aldığımız length eklemek istediğimiz text'den uzun değilse;
                    currentText.set(cursor,array[i]);// o karakteri Arraylistin cursorıncı indexine ekler.
                else // Eğer ki parametre olarak aldığımız length eklemek istediğimiz text'den kısaysa;
                    currentText.set(cursor,null);// Arraylistin cursorıncı elemanını null yapar(removedan farklıdır.)
                            // remove yapmadım çünkü undo ve redo yaparken de bu methodu kullanıcam ve arrayin sınırını aşacaktı
                cursor++;// Her işlemden sonra cursorı arttırır ki sile sile ve ekleye ekleye ilerlesin.
            }
            // Action classının constructorını kullanarak yeni bi action objesi oluşturur. Parametreye, action type olarak replacei,
            // newText olarak alınan text i, old texti olarak null silinen texti, ve alının position değişkenini alır.
            undoStack.push(new Action("replace",newText,oldText,position));
            clear();// replace işlemi yapıldıktan sonra redo yapılmasın diye redoStack'ini clearlar.
        }else {// İşlem yapılacak kısımın hepsi arraylistin sınırlarını aşıyorsa;
            throw new ArraySizeExceeded("Text boundary exceeded.");// Kendi oluşturduğum ArraySizeExceeded Exception'ını atar.
                                                // ve mainde mesajı yazdırmak için constructorın parametresi olarak mesaj girer
        }
    }

    // Parametre olarak herhangi bir şey almaz. En son yapılan işlemin türüne gore o işlemi geri alır zıt methodlarla.Eğer yine bu 
    // methodları kullanırken arrayin sınırı aşılırsa kendi eklediğim ArraySizeExceeded Exception'u atar.Hiç bir şey return etmez.
    public void undo()throws ArraySizeExceeded{
        if(undoStack.top()!=null){// Eğer undooStack'i boş değilse(top nullsa boştur);
            Action lastAction = undoStack.pop();// En son yapılan işlemi poplar ve bunu Action türündeki lastaction değişkeninde tutar.
            Action actionDone = new Action();// Yapılan işlemleri tutması için actiondone değişkenini initialize eder ve herhangi bir 
            // işlemden sonra bu action objesini değiştirip, redo yapmak için redoStack'ine pushlayacağım için şimdiden oluşturdum.
            if(lastAction.getType().equals("insert")){//Eğer son yapılan işlemin typeı insert ise;
                //delete methoduna, son işlemin posizyonunu ve son işlemde eklenen textin uzunluğunu girerek sileriz.
                delete(lastAction.getPosition(), lastAction.getNewText().length());
                //Bu yapılan geri alma işlemini, ileriye almak için redoStack'ine pushlanacak action objesini düzenler.
                //Geri alma işleminde silme işlemini yaptığımız için old text yerine lastactionda eklediğimiz texti ve pozisyonu alır.
                actionDone = new Action("delete",null,lastAction.getNewText(),lastAction.getPosition());
            }else if (lastAction.getType().equals("delete")){//Eğer son yapılan işlemin typeı delete ise;
                //insert methoduna, son işlemde silinen texti ve son işlemin yapıldığı pozisyonu girerek ekleriz.
                insert( lastAction.getOldText(), lastAction.getPosition());
                //Bu yapılan geri alma işlemini, ileriye almak için redoStack'ine pushlanacak action objesini düzenler.
                //Geri alma işleminde ekleme işlemini yaptığımız için newtext yerine lastactionda sildiğimiz texti ve pozisyonu alır.
                actionDone = new Action("insert",lastAction.getOldText(),null,lastAction.getPosition());
            }else if (lastAction.getType().equals("replace")){//Eğer son yapılan işlemin typeı insert ise;
                //replace methoduna, son işlemde replace edilen texti ,uzunluğunu ve pozisyonu girerek replace ederiz yine.
                replace(lastAction.getOldText(), lastAction.getPosition(), lastAction.getOldText().length());
                //Bu yine yapılan replace işlemini, ileriye almak için redoStack'ine pushlanacak action objesini düzenler.
                //Geri alma işleminde replace işlemini yaptığımız için newtext yerine lastactionda replace ettiğimiz texti,
                // old text yerine ilk replacede eklediğimiz texti, ve replace yapılan pozisyonu alır.
                actionDone = new Action("replace",lastAction.getOldText(),lastAction.getNewText(),lastAction.getPosition());
            }
            // ***ÖNEMLİ*** Her koşulda delete,insert,replace methodunu kullanacağımız için o methodların içinde fazladan eklenmeme-
            // ** -si gereken bi action objesi undoStack'e pushlanacak.Bu yüzden fazla olan objeyi pop eder.
            undoStack.pop();
            // Yukardaki işlemden sonra düzenlediğimiz action objesini redoStack'e pushlar ki redo yaptıgımızda bilgileri bundan alabilelim
            redoStack.push(actionDone);
            // undo yapıldığının bilgisini yazdırır
            System.out.println("Undo - Action: "+ lastAction.getType()+" - New Text: "+ lastAction.getOldText()+" - Old Text: "+lastAction.getNewText());
            printText(); // Her elemanı teker teker yazdırır her işlemden sonra güncel textin durumunu gostersin diye.
        }else{// Eğer undooStack'i boş ise(top nullsa boştur);
            System.out.println("There is nothing to undo! \n");// undoStack'inde undo yapmak için bi işlem olmadıgını yazdırır.
        }
    }
    // Parametre olarak herhangi bir şey almaz. En son yapılan undo işlemine gore o işlemi geri alır zıt methodlarla.Eğer yine bu 
    // methodları kullanırken arrayin sınırı aşılırsa kendi eklediğim ArraySizeExceeded Exception'u atar.Hiç bir şey return etmez.
    public void redo()throws ArraySizeExceeded{
        if(redoStack.top()!=null){// Eğer redoStack'i boş değilse(top nullsa boştur);
            Action lastAction = redoStack.pop();// En son yapılan işlemi poplar ve bunu Action türündeki lastaction değişkeninde tutar.
            Action actionDone = new Action();// Yapılan işlemleri tutması için actiondone değişkenini initialize eder ve herhangi bir 
            // işlemden sonra bu action objesini değiştirip, sonra undo yapmak için undoStack'ine pushlayacağım için şimdiden oluşturdum.
            if(lastAction.getType().equals("insert")){//Eğer son yapılan undo işlemin typeı insert ise;
                //delete methoduna, son işlemin posizyonunu ve son işlemde eklenen textin uzunluğunu girerek sileriz.
                delete(lastAction.getPosition(), lastAction.getNewText().length());
                //Bu yapılan geri alma işlemini, ileriye almak için redoStack'ine pushlanacak action objesini düzenler.
                //ileri alma işleminde silme işlemini yaptığımız için old text yerine lastactionda eklediğimiz texti ve pozisyonu alır.
                actionDone = new Action("delete","",lastAction.getNewText(),lastAction.getPosition());
            }else if (lastAction.getType().equals("delete")){//Eğer son yapılan undo işlemin typeı delete ise;
                //insert methoduna, son işlemde silinen texti ve son işlemin yapıldığı pozisyonu girerek ekleriz.
                insert( lastAction.getOldText(), lastAction.getPosition());
                //Bu yapılan geri alma işlemini, ileriye almak için redoStack'ine pushlanacak action objesini düzenler.
                //ileri alma işleminde ekleme işlemini yaptığımız için newtext yerine lastactionda sildiğimiz texti ve pozisyonu alır.
                actionDone = new Action("insert",lastAction.getOldText(),"",lastAction.getPosition());
            }else if (lastAction.getType().equals("replace")){//Eğer son yapılan undo işlemin typeı replace ise;
                //replace methoduna, son işlemde replace edilen texti ,uzunluğunu ve pozisyonu girerek replace ederiz yine.
                replace(lastAction.getOldText(), lastAction.getPosition(), lastAction.getNewText().length());
                //Bu yine yapılan replace işlemini, ileriye almak için undoStack'ine pushlanacak action objesini düzenler.
                //ileri alma işleminde replace işlemini yaptığımız için newtext yerine lastactionda replace ettiğimiz texti,
                // old text yerine ilk replacede eklediğimiz texti, ve replace yapılan pozisyonu alır.
                undoStack.push(new Action("replace",lastAction.getOldText(),lastAction.getNewText(),lastAction.getPosition()));
                
            }
            // ***ÖNEMLİ*** Her koşulda delete,insert,replace methodunu kullanacağımız için o methodların içinde fazladan eklenmeme-
            // ** -si gereken bi action objesi redoStack'e pushlanacak.Bu yüzden fazla olan objeyi pop eder.
            redoStack.pop();
            // Yukardaki işlemden sonra düzenlediğimiz action objesini undoStack'e pushlar ki tekrar undo yaptıgımızda bilgileri bundan alabilelim
            undoStack.push(actionDone);
            // redo yapıldığının bilgisini yazdırır
            System.out.println("Redo - Action: "+ lastAction.getType()+" - New Text: "+ lastAction.getOldText()+" - Old Text: "+lastAction.getNewText());
            printText(); // Her elemanı teker teker yazdırır her işlemden sonra güncel textin durumunu gostersin diye.
        }else{// Eğer redoStack'i boş ise(top nullsa boştur);
            System.out.println("There is nothing to redo!\n");// redoStack'inde redo yapmak için bi işlem olmadıgını yazdırır.
        }
    }

    // Parametre olarak bir şey almaz. Her bir işlemden sonra(undo redo hariç) redo yapılmasın diye redoStack'i temizler.
    public void clear(){
        while(!redoStack.isEmpty()){// redoStack'i boş olmadığı sürece;
            redoStack.pop();        // redoStacki'ini teker teker poplayıp temizler.
        }
    }

    // Bunu sadece Final texti yazdırmak için yazdım. Arraylistin tüm karakterlerini birleştirip return eder.
    public String toString(){
        String print=""; // return edilecek olan string tutan string değişkenini initialize eder.
        if(!currentText.isEmpty()){// Arraylist boş değilse;
            for(int i=0; i<currentText.size();i++){ // arraylistin tüm elemanlarını döner
                if(currentText.get(i)!=null){ // eğer güncel textin i. elemanı boş değilse;
                    print += currentText.get(i);// return edeceğimiz değişkene textimizin i. karakterini ekler
                }
                else{// eğer güncel textin i. elemanı boşsa;
                    print += " ";// boş bi karakter ekler. 
                }
            }
        }// Boşsa hiçbir şey eklenmediği için direkt ilk halini(boş) return etmesi lazım;
        return print; //Düzenlenen string değişkenini return eder.
    }

    //Bu methodu indexleri görmek kolay olsun diye yazdım. Güncel textin her bir karakterini [] içerisinde yazdırır.
    // Parametre olarak bir şey almasına gerek yoktur texti görebiliyor zaten. Direkt yazdıreacağı için bir şey return etmez.
    public void printText(){
        for(int i=0;i<currentText.size();i++){// arraylistin tüm elemanlarını döner
            if(currentText.get(i)!= null) // eğer güncel textin i. elemanı boş değilse;
                System.out.print("["+currentText.get(i)+"]");//textimizin i. karakterini yazdırır koşeli parantez içerisinde
            else // eğer güncel textin i. elemanı boşsa;
                System.out.print("[ ]");// Boş bi karakter yazdırır
        }
        System.out.println();System.out.println();// System.out.print kullandığım için yazdırma işlemi bittiğinde bi alt satıra geçsin
        // ve fazladan bi satır boşluk bıraksın diye.
    }

    // Kendi oluşturduğum Exception. Sadece yapılan işlemler etkilediği kısım arrayin sınırını aşarsa fırlatılır.
    // Exception classını extend eder.
    class ArraySizeExceeded extends Exception {
        public ArraySizeExceeded(String x) {
            super(x);// Exception classının constructorına parametre olarak alınan x stringini girer.
        }
    }
}