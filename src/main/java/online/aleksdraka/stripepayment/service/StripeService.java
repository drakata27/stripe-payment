//package online.aleksdraka.stripepayment.service;
//
//import com.stripe.Stripe;
//import com.stripe.exception.StripeException;
//import com.stripe.model.checkout.Session;
//import com.stripe.param.checkout.SessionCreateParams;
//import online.aleksdraka.stripepayment.dto.ProductRequest;
//import online.aleksdraka.stripepayment.dto.StripeResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.util.logging.Logger;
//
//@Service
//public class StripeService {
//
//    private final Logger logger = Logger.getLogger(StripeService.class.getName());
//    @Value("${stripe.secretKey}")
//    private String secretKey;
//
//    public StripeResponse checkoutProducts(ProductRequest productRequest) {
//        Stripe.apiKey = secretKey;
//
//        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                .setName(productRequest.getName())
//                .setDescription(productRequest.getDescription())
//                .build();
//
//        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
//                .setCurrency(productRequest.getCurrency() == null ? "GBP" : productRequest.getCurrency())
//                .setUnitAmount(productRequest.getAmount().longValue())
//                .setProductData(productData)
//                .build();
//
//        // Create new line item with the above price data
//        SessionCreateParams.LineItem lineItem =
//                SessionCreateParams
//                        .LineItem.builder()
//                        .setQuantity(productRequest.getQuantity())
//                        .setPriceData(priceData)
//                        .build();
//
//        // Create new session with the line items
//        SessionCreateParams params =
//                SessionCreateParams.builder()
//                        .setMode(SessionCreateParams.Mode.PAYMENT)
//                        .setSuccessUrl("http://localhost:8080/success")
//                        .setCancelUrl("http://localhost:8080/cancel")
//                        .addLineItem(lineItem)
//                        .build();
//
//        // Create new session
//        Session session = null;
//        try {
//            session = Session.create(params);
//        } catch (StripeException e) {
//            //log the error
//            logger.warning(e.getMessage());
//        }
//
//        return StripeResponse
//                .builder()
//                .status("SUCCESS")
//                .message("Payment session created ")
//                .sessionId(session.getId())
//                .sessionUrl(session.getUrl())
//                .build();
//    }
//}

package online.aleksdraka.stripepayment.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import online.aleksdraka.stripepayment.dto.ProductRequest;
import online.aleksdraka.stripepayment.dto.StripeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class StripeService {

    private final Logger logger = Logger.getLogger(StripeService.class.getName());

    @Value("${stripe.secretKey}")
    private String secretKey;

    public StripeResponse checkoutProducts(List<ProductRequest> productRequests) {
        Stripe.apiKey = secretKey;

        // Create line items for each product
        List<SessionCreateParams.LineItem> lineItems = productRequests.stream().map(productRequest -> {
            // Create Product Data
            SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                    .setName(productRequest.getName())
                    .setDescription(productRequest.getDescription())
                    .build();

            // Create Price Data
            SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                    .setCurrency(productRequest.getCurrency() == null ? "GBP" : productRequest.getCurrency())
                    .setUnitAmount(productRequest.getAmount().longValue())
                    .setProductData(productData)
                    .build();

            // Create Line Item
            return SessionCreateParams.LineItem.builder()
                    .setQuantity(productRequest.getQuantity())
                    .setPriceData(priceData)
                    .build();
        }).collect(Collectors.toList());

        // Create new session with all the line items
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:8080/success")
                .setCancelUrl("http://localhost:8080/cancel")
                .addAllLineItem(lineItems)
                .build();

        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            logger.warning(e.getMessage());
            return StripeResponse.builder()
                    .status("FAILURE")
                    .message("Failed to create payment session")
                    .build();
        }

        return StripeResponse.builder()
                .status("SUCCESS")
                .message("Payment session created")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }
}
