package ru.sunlab.shopemailinformer.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import ru.sunlab.shopemailinformer.configuration.RabbitConfig;
import ru.sunlab.shopemailinformer.dto.rabbit.OrderItemRmqDto;
import ru.sunlab.shopemailinformer.dto.rabbit.OrderRmqDto;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RmqListener {

    private final EmailService emailService;
    @Autowired
    public RmqListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_EMAIL)
    public void listener(OrderRmqDto ord, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag){
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("\t Добрый день, ").append(ord.getFirstName()).append(" ").append(ord.getMiddleName()).append("!\n")
                    .append("На ваше имя был оформлен заказ №-").append(ord.getOrderId()).append("\n")
                    .append("Перечень товаров:\n");

            List<OrderItemRmqDto> newOirds = new ArrayList<>();
            for (OrderItemRmqDto oird : ord.getOrderItemRmqDtos()){
                Optional<OrderItemRmqDto> first = newOirds.stream()
                        .filter(nOird -> nOird.getProductName().equals(oird.getProductName()))
                        .findFirst();

                if (first.isPresent()){
                    OrderItemRmqDto o = first.get();
                    o.setQuantity(o.getQuantity() + oird.getQuantity());
                } else {
                    newOirds.add(oird);
                }
            }

            for (OrderItemRmqDto oird : newOirds) {
                sb.append("\t").append(oird.getProductName()).append(" количество ").append(oird.getQuantity())
                        .append("шт. \tцена ").append(oird.getPrice()).append(" руб/шт.\n");
            }
            sb.append("\tОбщая стоимость заказа - ").append(ord.getSummaryCost()).append("руб.\n");

            if (ord.getDeliveryTimes() != null){
                sb.append("Товар будет подготовлен к выдачи ").append(ord.getDeliveryTimes().toLocalDate())
                        .append(" до ").append(ord.getDeliveryTimes().toLocalTime().getHour()).append(":00")
                        .append(" в магазине ").append(ord.getStoreName()).append("\n")
                        .append("По готовности с вами свяжется один из наших менеджеров.");
            } else {
                sb.append("Товар готов к выдаче в магазине ").append(ord.getStoreName()).append(".\n");
            }
                    sb.append("\n\n\tСпасибо что остаётесь с нами ;)");

            emailService.sendMail(ord.getEmail(),
                    "Заказ-"+ord.getOrderId(),
                    sb.toString());

            channel.basicAck(tag, false);
        } catch (MailException e) {
            log.error(e.getMessage());
            try {
                channel.basicNack(tag,false,true);
                Thread.sleep(3000);
            } catch (InterruptedException | IOException ex) {
                log.error(ex.getMessage());
            }
        } catch (IOException e){
            log.error(e.getMessage());
/*
            Это исключение бросается когда сообщение о заказе было отправлено заказчику
            но подтверждение о доставке "Кролику" не было доставлено.
            Нужно уже отправленные сообщения временно хранить/кешировать и сравнивать с вновь
            пришедшими на наличие дублей.
*/
        }
    }
}
