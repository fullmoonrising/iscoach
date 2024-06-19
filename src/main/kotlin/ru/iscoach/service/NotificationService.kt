package ru.iscoach.service

import jakarta.mail.internet.InternetAddress
import mu.KLogging
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.iscoach.ISCoachBot
import ru.iscoach.config.BotCredentialsConfig
import ru.iscoach.extrensions.format
import ru.iscoach.persistence.entity.toDto
import ru.iscoach.persistence.repos.NotificationRepo
import ru.iscoach.service.model.ProductCategory
import ru.iscoach.service.model.entity.OrderInfo

@Service
class NotificationService(
    private val bot: ISCoachBot,
    private val javaMailSender: JavaMailSender,
    private val botCredentials: BotCredentialsConfig,
    private val notificationRepo: NotificationRepo
) : KLogging() {
    private val photoPath = "/static/images/OrderReceived.png"

    fun sendNotifications(orderInfo: OrderInfo) {
        notificationRepo.findAll().map { it.toDto() }.forEach {
            sendSellerTelegramNotification(orderInfo, it.userId)
            sendSellerMailNotification(orderInfo, it.email)
        }
        if (orderInfo.payload.product.category != ProductCategory.DIGITAL_GOODS) {
            sendCustomerMailNotification(orderInfo)
        }
    }

    private fun sendSellerTelegramNotification(orderInfo: OrderInfo, messageTo: Long) {
        try {
            bot.execute(buildSellerTelegramNotification(orderInfo, messageTo))
            logger.info { "Send order notification to $messageTo" }
        } catch (ex: TelegramApiException) {
            logger.error(ex) { "Error occurred while sending telegram notification for order №${orderInfo.id} to $messageTo" }
        }
    }

    private fun buildSellerTelegramNotification(orderInfo: OrderInfo, messageTo: Long): SendPhoto {
        return SendPhoto
            .builder()
            .parseMode(ParseMode.HTML)
            .chatId(messageTo)
            .photo(InputFile(this::class.java.getResourceAsStream(photoPath), photoPath.substringAfterLast("/")))
            .caption(buildSellerCaption(orderInfo))
            .build()
    }


    private fun buildSellerCaption(orderInfo: OrderInfo) =
        """
            <b>Получен новый заказ №${orderInfo.id} с помощью <a href="tg://user?id=${bot.me.id}">бота</a></b>
            <b>Детали заказа:</b>
            <i>Услуга:</i> <b>${orderInfo.payload.product.label}</b>
            <i>Оплаченная сумма:</i> <b>${(orderInfo.payment.totalAmount / 100.0).format()}₽</b>
            <i>Клиент:</i>
                <i>Имя:</i> <b>${orderInfo.payment.orderInfo?.name.orEmpty()}</b>
                <i>Телефон:</i> <b>+${orderInfo.payment.orderInfo?.phoneNumber.orEmpty()}</b>
                <i>Почта:</i> <b>${orderInfo.payment.orderInfo?.email.orEmpty()}</b>
        """.trimIndent()

    private fun sendSellerMailNotification(orderInfo: OrderInfo, mailTo: String) {
        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message)
        helper.setFrom(InternetAddress(botCredentials.botMail, "ISCoach бот"))
        helper.setTo(mailTo)
        helper.setSubject("Оформлен заказ №${orderInfo.id} с помощью бота")
        helper.setText(buildSellerMailBody(orderInfo), true)
        javaMailSender.send(message)
        logger.info { "Send order notification to $mailTo" }
    }

    private fun buildSellerMailBody(orderInfo: OrderInfo) =
        """
            <b>Получен новый заказ №${orderInfo.id} с помощью <a href="tg://user?id=${bot.me.id}">бота</a></b><br>
            <b>Детали заказа:</b><br>
            <i>Услуга:</i> <b>${orderInfo.payload.product.label}</b><br>
            <i>Оплаченная сумма:</i> <b>${(orderInfo.payment.totalAmount / 100.0).format()}₽</b><br>
            <i>Клиент:</i><br>
            &emsp;&emsp;<i>Имя:</i> <b>${orderInfo.payment.orderInfo?.name.orEmpty()}</b><br>
            &emsp;&emsp;<i>Телефон:</i> <b>+${orderInfo.payment.orderInfo?.phoneNumber.orEmpty()}</b><br>
            &emsp;&emsp;<i>Почта:</i> <b>${orderInfo.payment.orderInfo?.email.orEmpty()}</b><br>
        """.trimIndent()

    private fun sendCustomerMailNotification(orderInfo: OrderInfo) {
        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message)
        helper.setFrom(InternetAddress(botCredentials.botMail, "ISCoach бот"))
        helper.setTo(orderInfo.payment.orderInfo.email)
        helper.setSubject("Заказ №${orderInfo.id} iscoach.ru")
        helper.setText(buildCustomerSellerMailBody(orderInfo), true)
        javaMailSender.send(message)
        logger.info { "Send order notification to ${orderInfo.payment.orderInfo.email}" }
    }

    private fun buildCustomerSellerMailBody(orderInfo: OrderInfo) =
        """
            <b>Получен новый заказ №${orderInfo.id} с помощью <a href="tg://user?id=${bot.me.id}">бота</a></b><br>
            <b>Детали заказа:</b><br>
            <i>Услуга:</i> <b>${orderInfo.payload.product.label}</b><br>
            <i>Оплаченная сумма:</i> <b>${(orderInfo.payment.totalAmount / 100.0).format()}₽</b><br>
            Мы свяжемся с вами в ближайшее время для уточнения деталей
        """.trimIndent()
}