package bookrental;

import bookrental.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{

    @Autowired
    BookRentalSystemRepository brs;

    @Autowired
    BookListStatusRepository bls;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverBookRegistered_ChangeBookStatus(@Payload BookRegistered bookRegistered){

        if(bookRegistered.isMe()){
            System.out.println("##### listener ChangeBookStatus : " + bookRegistered.toJson());

            BookListStatus bookStatus = new BookListStatus();
            bookStatus.setBookName(bookRegistered.getBookName());
            bookStatus.setRentalFee(bookRegistered.getRentalFee());
            bookStatus.setId(bookRegistered.getId());
            bookStatus.setRentalStatus("IDLE");

            bls.save(bookStatus);
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_ChangeBookStatus(@Payload Paid paid){

        if(paid.isMe()){
            System.out.println("##### listener ChangeBookStatus : " + paid.toJson());

            Optional<BookRentalSystem> bookRentalSystemOptional = brs.findById(paid.getRentalId());
            if( bookRentalSystemOptional.isPresent() ) {
                BookRentalSystem bookRental = bookRentalSystemOptional.get();

                Optional<BookListStatus> bookListStatusOptional = bls.findById(bookRental.getBookId());
                if( bookListStatusOptional.isPresent() ){
                    BookListStatus bookStatus = bookListStatusOptional.get();

                    bookStatus.setRentalStatus("RENTED");

                    bls.save(bookStatus);
                }
            }
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRefunded_ChangeBookStatus(@Payload Refunded refunded){

        if(refunded.isMe()){
            System.out.println("##### listener ChangeBookStatus : " + refunded.toJson());

            Optional<BookRentalSystem> bookRentalSystemOptional = brs.findById(refunded.getRentalId());
            if( bookRentalSystemOptional.isPresent() ) {
                BookRentalSystem bookRental = bookRentalSystemOptional.get();

                Optional<BookListStatus> bookListStatusOptional = bls.findById(bookRental.getBookId());
                if( bookListStatusOptional.isPresent() ){
                    BookListStatus bookStatus = bookListStatusOptional.get();

                    bookStatus.setRentalStatus("IDLE");

                    bls.save(bookStatus);
                }
            }
        }
    }

}
