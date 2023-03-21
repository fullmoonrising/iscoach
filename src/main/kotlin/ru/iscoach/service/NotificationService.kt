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
import ru.iscoach.service.model.dto.OrderInfoDto

@Service
class NotificationService(
    private val bot: ISCoachBot,
    private val javaMailSender: JavaMailSender,
    private val botCredentials: BotCredentialsConfig,
    private val notificationRepo: NotificationRepo
) : KLogging() {
    private val photoPath = "/static/images/OrderReceived.png"

    fun sendNotifications(orderInfo: OrderInfoDto) {
        notificationRepo.findAll().map { it.toDto() }.forEach {
            sendTelegramNotification(orderInfo, it.userId)
            sendMailNotification(orderInfo, it.email)
        }
    }

    private fun sendTelegramNotification(orderInfo: OrderInfoDto, messageTo: Long) {
        try {
            bot.execute(buildTelegramNotification(orderInfo, messageTo))
            logger.info { "Send order notification to $messageTo" }
        } catch (ex: TelegramApiException) {
            logger.error(ex) { "Error occurred while sending telegram notification for order №${orderInfo.id} to $messageTo" }
        }
    }

    private fun buildTelegramNotification(orderInfo: OrderInfoDto, messageTo: Long): SendPhoto {
        return SendPhoto
            .builder()
            .parseMode(ParseMode.HTML)
            .chatId(messageTo)
            .photo(InputFile(this::class.java.getResourceAsStream(photoPath), photoPath.substringAfterLast("/")))
            .caption(buildCaption(orderInfo))
            .build()
    }


    private fun buildCaption(orderInfo: OrderInfoDto) =
        """
            <b>Получен новый заказ №${orderInfo.id} с помощью <a href="tg://user?id=${bot.me.id}">бота</a></b>
            <b>Детали заказа:</b>
            <i>Тип:</i> <b>${orderInfo.payload.product.id.label}</b>
            <i>Оплаченная сумма:</i> <b>${(orderInfo.payment.totalAmount / 100.0).format()}₽</b>
            <i>Клиент:</i>
                <i>Имя:</i> <b>${orderInfo.payment.orderInfo.name}</b>
                <i>Телефон:</i> <b>+${orderInfo.payment.orderInfo.phoneNumber}</b>
                <i>Почта:</i> <b>${orderInfo.payment.orderInfo.email}</b>
        """.trimIndent()

    private fun sendMailNotification(orderInfo: OrderInfoDto, mailTo: String) {
        val message = javaMailSender.createMimeMessage()
        val helper = MimeMessageHelper(message)
        helper.setFrom(InternetAddress(botCredentials.botMail, "ISCoach бот"))
        helper.setTo(mailTo)
        helper.setSubject("Оформлен заказ №${orderInfo.id} с помощью бота")
        helper.setText(buildMailBody(orderInfo), true)
        javaMailSender.send(message)
        logger.info { "Send order notification to $mailTo" }
    }

    private fun buildMailBody(orderInfo: OrderInfoDto) =
        """
            <b>Получен новый заказ №${orderInfo.id} с помощью <a href="tg://user?id=${bot.me.id}">бота</a></b><br>
            <b>Детали заказа:</b><br>
            <i>Тип:</i> <b>${orderInfo.payload.product.id.label}</b><br>
            <i>Оплаченная сумма:</i> <b>${(orderInfo.payment.totalAmount / 100.0).format()}₽</b><br>
            <i>Клиент:</i><br>
            &emsp;&emsp;<i>Имя:</i> <b>${orderInfo.payment.orderInfo.name}</b><br>
            &emsp;&emsp;<i>Телефон:</i> <b>+${orderInfo.payment.orderInfo.phoneNumber}</b><br>
            &emsp;&emsp;<i>Почта:</i> <b>${orderInfo.payment.orderInfo.email}</b><br>
        """.trimIndent()
}