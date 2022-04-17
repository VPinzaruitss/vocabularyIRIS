package com.itss.irisvoc;

import com.itss.irisvoc.entity.Vocabulary;
import com.itss.irisvoc.handlers.EnquiryHandler;
import com.itss.irisvoc.handlers.Handler;
import com.itss.irisvoc.handlers.VersionHandler;
import com.itss.irisvoc.services.HelpTextService;
import com.itss.irisvoc.services.VocabularyService;
import com.itss.t24runtime.T24Runtime;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        Vocabulary vocabulary = new Vocabulary();

        Vocabulary.Entries partyDomain = new Vocabulary.Entries();
        partyDomain.setKey("party");
        partyDomain.setEntryType("domain");
        vocabulary.getEntries().add(partyDomain);

        HelpTextService.getDescriptions();

        try (T24Runtime runtime = T24Runtime.getNotInitialized()) {

            new VersionHandler().handleTable(runtime, "F.VERSION", VersionHandler.VERSION_RECORD_PATTERN);
            new EnquiryHandler().handleTable(runtime, "F.ENQUIRY", EnquiryHandler.ENQUIRY_RECORD_PATTERN);

        } catch (Exception e) {
            e.printStackTrace();
        }

        vocabulary.getEntries().addAll(Handler.listForAdd);

        try {
            VocabularyService.serializationIntoJson(vocabulary);
        } catch (IOException e) {

            e.printStackTrace();
            System.exit(1);

        }
    }
}