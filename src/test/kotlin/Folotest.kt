import com.telegram.folobot.FoloBot
import com.telegram.folobot.service.FoloPidorService
import com.telegram.folobot.service.FoloUserService
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberBanned
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberLeft
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberOwner

class Folotest(

) {
    @Test
    fun userState() {
        val chatMember: ChatMember? = null
//        val chatMember: ChatMember? = ChatMemberOwner()
        val aloe = chatMember?.let { !(it.status == "left" || it.status == "kicked") } ?: false
        println(aloe)
    }
}