import android.view.View
import android.widget.ImageView
import com.app.easy_patient.R

fun setParallaxTransformation(page: View, position: Float){
    val img = page.findViewById<ImageView>(R.id.img)
    page.apply {
        when {
            position < -1 -> // [-Infinity,-1)
                // This page is way off-screen to the left.
                alpha = 1f
            position <= 1 -> { // [-1,1]
                img.translationX = -position * (width / 2) //Half the normal speed
            }
            else -> // (1,+Infinity]
                // This page is way off-screen to the right.
                alpha = 1f
        }
    }

}