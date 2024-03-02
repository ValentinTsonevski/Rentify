package com.devminds.rentify.service;

import com.devminds.rentify.dto.StripeAdditionalInfoDto;
import com.devminds.rentify.entity.Item;
import com.devminds.rentify.entity.Payment;
import com.devminds.rentify.entity.Rent;
import com.devminds.rentify.entity.User;
import com.devminds.rentify.enums.PaymentMethod;
import com.devminds.rentify.enums.PaymentStatus;
import com.devminds.rentify.repository.ItemRepository;
import com.devminds.rentify.repository.PaymentRepository;
import com.devminds.rentify.repository.RentRepository;
import com.devminds.rentify.repository.UserRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.Product;
import com.stripe.model.Token;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.TokenCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Date;
import java.util.List;

import com.stripe.model.Event;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe-key}")
    private String key;

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RentRepository rentRepository;
    private final PaymentRepository paymentRepository;

    private static final String[] HEADERS_TO_TRY = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"};

    public String getClientIpAddress(HttpServletRequest request) {
        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    public Account createStripeAccount(HttpServletRequest httpServletRequest, Long userId) throws StripeException {
        Stripe.apiKey = key;

        User principal = userRepository.findById(userId).orElse(null);

        TokenCreateParams tokenParams =
                TokenCreateParams.builder()
                        .setBankAccount(
                                TokenCreateParams.BankAccount.builder()
                                        .setCountry("BG")
                                        .setCurrency("bgn")
                                        .setAccountHolderName(principal.getFirstName() + " " + principal.getLastName())
                                        .setAccountHolderType(
                                                TokenCreateParams.BankAccount.AccountHolderType.INDIVIDUAL
                                        )
                                        .setAccountNumber(principal.getIban())
                                        .build()
                        )
                        .build();
        Token token = Token.create(tokenParams);

        AccountCreateParams params =
                AccountCreateParams.builder()
                        .setType(AccountCreateParams.Type.CUSTOM)
                        .setCountry("BG")
                        .setEmail(principal.getEmail())
                        .setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL)
                        .setBusinessProfile(AccountCreateParams.BusinessProfile.builder()
                                .setMcc("5931")
                                .setProductDescription("marketplaces").build())
                        .setIndividual(AccountCreateParams.Individual.builder()
                                .setFirstName(principal.getFirstName())
                                .setLastName(principal.getLastName())
                                .setDob(AccountCreateParams.Individual.Dob.builder().setYear(2000L)
                                        .setMonth(9L)
                                        .setDay(9L).build())
                                .setAddress(AccountCreateParams.Individual.Address.builder()
                                        .setCountry("BG")
                                        .setCity(principal.getAddress().getCity())
                                        .setLine1(principal.getAddress().getStreet() + " " + principal.getAddress().getStreetNumber())
                                        .setState(principal.getAddress().getCity())
                                        .setPostalCode(principal.getAddress().getPostCode()).build())
                                .setEmail(principal.getEmail())
                                .setPhone(principal.getPhoneNumber())
                                .build())
                        .setTosAcceptance(AccountCreateParams.TosAcceptance.builder()
                                .setIp(getClientIpAddress(httpServletRequest))
                                .setDate(new Date().getTime() / 1000)
                                .build())

                        .setExternalAccount(token.getId())

                        .setCapabilities(
                                AccountCreateParams.Capabilities.builder()
                                        .setCardPayments(
                                                AccountCreateParams.Capabilities.CardPayments.builder()
                                                        .setRequested(true)
                                                        .build()
                                        )
                                        .setTransfers(
                                                AccountCreateParams.Capabilities.Transfers.builder()
                                                        .setRequested(true)
                                                        .build()
                                        )
                                        .build()
                        ).build();


        if (principal.getStripeAccountId() == null) {
            Account account = Account.create(params);
            principal.setStripeAccountId(account.getIndividual().getAccount());
            account.setPayoutsEnabled(true);
            userRepository.save(principal);
            return account;
        }

        Account account = Account.retrieve(principal.getStripeAccountId());
        account.setPayoutsEnabled(true);
        return account;
    }


    public Product createProduct(Item item, List<URL> pictureUrls) throws StripeException {
        Stripe.apiKey = key;

        ProductCreateParams params =
                ProductCreateParams.builder().setName(item.getName())
                        .setDefaultPriceData(ProductCreateParams.DefaultPriceData.builder().setUnitAmount(item.getPrice().longValueExact() * 100L).setCurrency("usd").build())
                        .addImage(pictureUrls.get(0).toString()).build();


        Product product = Product.create(params);

        item.setItemStripeId(product.getDefaultPrice());
        itemRepository.save(item);

        return product;
    }


    public String createCheckoutSession(Long itemId, StripeAdditionalInfoDto stripeAdditionalInfoDto) throws StripeException {
        Stripe.apiKey = key;
        Period period = Period.between(stripeAdditionalInfoDto.getStartDate(), stripeAdditionalInfoDto.getEndDate());
        int rentDays = period.getDays() + 1;

        Item item = itemRepository.findById(itemId).orElse(null);
        User itemOwner = userRepository.findById(item.getUser().getId()).orElse(null);
        User currentLoggedInUser = userRepository.findById(stripeAdditionalInfoDto.getUserId()).orElse(null);

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setCustomerEmail(currentLoggedInUser.getEmail())
                        .putMetadata("item_id", itemId.toString())
                        .putMetadata("start_date", stripeAdditionalInfoDto.getStartDate().toString())
                        .putMetadata("end_date", stripeAdditionalInfoDto.getEndDate().toString())
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPrice(item.getItemStripeId())
                                        .setQuantity((long) rentDays)
                                        .build()
                        )
                        .setPaymentIntentData(
                                SessionCreateParams.PaymentIntentData.builder()
                                        .setApplicationFeeAmount(1000L)
                                        .setTransferData(
                                                SessionCreateParams.PaymentIntentData.TransferData.builder()
                                                        .setDestination(itemOwner.getStripeAccountId())
                                                        .build()
                                        )
                                        .build()
                        ).putMetadata("item_id", itemId.toString())
                        .setSuccessUrl("http://localhost:3000/")
                        .setCancelUrl("https://example.com/cancel")
                        .build();

        Session session = Session.create(params);
        return session.getUrl();
    }


    public void hook(String payload, String sigHeader) throws JsonSyntaxException, SignatureVerificationException {
        Stripe.apiKey = key;
        String endpointSecret = "whsec_2bc75bd6e9c43409a2f9ec5023d7aa92ab13e83612f764c83f5833cd49e9c766";

        Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);


        if (event.getType().equals("checkout.session.completed")) {
            JsonObject test = JsonParser.parseString(payload).getAsJsonObject();

            JsonElement data = test.get("data");
            JsonElement data1 = data.getAsJsonObject().get("object");
            JsonElement metadata = data1.getAsJsonObject().get("metadata");
            JsonElement customerDetails = data1.getAsJsonObject().get("customer_details");


            JsonElement startDateJson = metadata.getAsJsonObject().get("start_date");
            JsonElement endDateJson = metadata.getAsJsonObject().get("end_date");

            JsonElement itemId = metadata.getAsJsonObject().get("item_id");
            JsonElement price = data1.getAsJsonObject().get("amount_total");
            JsonElement email = customerDetails.getAsJsonObject().get("email");

            String emailToString = email.toString().replace("\"", "");
            String endDate = endDateJson.toString().replace("\"", "");
            String startDate = startDateJson.toString().replace("\"", "");

            LocalDate startDateToSave = LocalDate.parse(startDate);
            LocalDate endDateToSave = LocalDate.parse(endDate);


            User buyer = userRepository.findByEmail(emailToString).orElse(null);
            Item item = itemRepository.findById(itemId.getAsLong()).orElse(null);
            User itemOwner = userRepository.findById(item.getUser().getId()).orElse(null);


            Rent rent = new Rent();
            rent.setUser(buyer);
            rent.setItem(item);
            rent.setStartDate(startDateToSave);
            rent.setEndDate(endDateToSave);
            Rent rentToSave = rentRepository.save(rent);

            Payment payment = new Payment();
            payment.setAmount(price.getAsBigDecimal().divide(BigDecimal.valueOf(100)));
            payment.setStatus(PaymentStatus.ACCEPTED);
            payment.setDate(LocalDateTime.now());
            payment.setOwner(buyer);
            payment.setReceiver(itemOwner);
            payment.setPaymentMethod(PaymentMethod.STRIPE);
            payment.setRent(rentToSave);
            paymentRepository.save(payment);


        }
    }


}
