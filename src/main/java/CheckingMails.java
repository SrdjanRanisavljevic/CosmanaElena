import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.mail.*;
import java.io.*;
import java.util.Date;
import java.util.Properties;

public class CheckingMails {

    public Message fetch(String pop3Host, String storeType, String user,
                             String password) {

        Message returnedMessage = null;
        String cs = "";
        try {
            // create properties field
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "pop3");
            properties.put("mail.pop3.host", pop3Host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);
            // emailSession.setDebug(true);

            // create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("pop3s");
            store.connect(pop3Host, user, password);

            // create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);


            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);

            returnedMessage =  messages[(messages.length-1)];
//            cs = writePart(messages[(messages.length - 1)]);


            // close the store and folder objects
//            emailFolder.close(false);
//            store.close();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return returnedMessage;
        }
    }



             // This method checks for content-type based on which, it processes and fetches the content of the message

    public String writePart(Part p) throws Exception {
        if (p instanceof Message)
            //Call methods writeEnvelope
            writeEnvelope((Message) p);

        if (p.isMimeType("multipart/*")) {
            System.out.println("This is a Multipart");
            System.out.println("---------------------------");
            Multipart mp = (Multipart) p.getContent();

            String strMultiPart  = (String) mp.getBodyPart(1).getContent();
            Document docMultiPart = Jsoup.parse(strMultiPart);
            Element magicLinkURL = docMultiPart.select("a").first();
            String parsedLink = magicLinkURL.toString();
            String codeSnippet = parsedLink.substring(72,106);
            return codeSnippet;


        }
        else {
            String html = (String) p.getContent();
            Document doc = Jsoup.parse(html);
            Element magicLinkURL= doc.select("a").first();
            String parsedLink = magicLinkURL.toString();
            String codeSnippet = parsedLink.substring(72,106);
//            System.out.println(codeSnippet);
            return codeSnippet;
        }

    }


//      This method would print SENDDATE, FROM,TO and SUBJECT of the message

    public static void writeEnvelope(Message m) throws Exception {


        Address[] a;

        //SEND DATE ideja je da napravim da iscita trenutno vreme kad je krenula skripta
//        i da u toj skripti uporedi vreme i da sendDate bude obavezno veci od vremena kad je krenula skripta!
        Date sendDate = m.getSentDate();
        System.out.println(sendDate);

        // FROM - cilj je da proveri i ovo pre nego sto krene bilo sta drugo
        if ((a = m.getFrom()) != null) {
            for (int j = 0; j < a.length; j++)
                System.out.println("FROM: " + a[j].toString());
        }

        // TO
        if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
            for (int j = 0; j < a.length; j++)
                System.out.println("TO: " + a[j].toString());
        }

        // SUBJECT
        if (m.getSubject() != null)
            System.out.println("SUBJECT: " + m.getSubject());
    }

    // This method retrieves sendDate for needed for checking the email with valid magic link

    public Date retrieveSendDate (Message m) throws Exception {

        Address[] a;

        Date sendDate = m.getSentDate();
        System.out.println(sendDate);
        return sendDate;
    }

    public String getSender (Message m) throws Exception {
        Address[] a;
        String sender = "";
        if ((a = m.getFrom()) != null) {
            for (int j = 0; j < a.length; j++)
                sender =  a[j].toString();
        }
        return sender;

    }


}





//TREBA MI PRVO CLASSA KOJA CE DA PROVERAVA SEND TIME I SENDER
// AKO SU SENDTIME > ScriptStart time && FROM = janrain@gmail.com) nastavi na cupanje maila
// CHEKAJ NA janrain@gmail.com i to vreme koje je vec od sendtimea


