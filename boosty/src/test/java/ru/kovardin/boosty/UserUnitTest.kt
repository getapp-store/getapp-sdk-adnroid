package ru.kovardin.boosty

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class UserUnitTest {
    @Test
    fun parse() {
        val user = User.parse("_clientId=a8f55b07-fe37-4421-a74d-9e7a5459924d; tmr_lvid=26c1d27c98fa42df08f63c17d7647a13; tmr_lvidTS=1697326574270; _gcl_au=1.1.827698951.1697326574; _ym_uid=1697326575876351029; _ym_d=1697326575; _fbp=fb.1.1697326574551.1718133325; _tt_enable_cookie=1; _ttp=br8sewrluFPKTqT2xFH1Nv_f9p-; auth=%7B%22accessToken%22%3A%22bc7d42136easdsd226c04cc532331ef007db883453968002ef12b05573e57d75bc32%22%2C%22refreshToken%22%3A%223f281281ce2dd4wdfwefd52f6b84d793ba92d8b724c820bd917da37b39bd3abd4bd517%22%2C%22expiresAt%22%3A1698354645649%7D; 1ltr_reg=1; last_acc={%22name%22:%22Artem%20Kovardin%22%2C%22avatarUrl%22:%22https://images.boosty.to/user/23651380/avatar?change_time=1697749845%22%2C%22provider%22:%22google%22%2C%22phone%22:null}; mr1lad=653303b6747f0343-0-0-; _ym_isad=2; tmr_detect=0%7C1697842104793")

        println("external: ${user?.external()}")

        Assert.assertEquals("23651380", user?.external())
    }
}