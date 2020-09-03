package yzl.swu.drawlogin

import android.animation.ObjectAnimator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_slide_login.*

class SlideLoginActivity : AppCompatActivity() {
    //记录绘制的密码
    private var password:String? = null
    //记录初始密码
    private var orgPassword:String? = null
    //确认密码  设置密码时
    private var firstSurePassword:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide_login)

        //获取初始密码
        orgPassword = SharedPreferenceUtil.getInstance(this).getPassword()
        if (orgPassword == null){
            mAlert.text = "请设置密码"
        }else{
            mAlert.text = "请绘制密码"
        }

//        mSlideUnlock.passwordBlock {
//
//            Log.v("yyy","pwd:11111")
//        }


        mSlideUnlock.oPasswordListenner = (object :SlideUnlockView.OnPasswordChangedListenner{
            override fun passwordChanged(pwd: String) {
                Log.v("yyy","pwd is:$pwd")
                password = pwd
                passwordOperation()
            }
        })

    }


    //判断密码操作
    private fun passwordOperation() {
        //头像旋转
        mHeader.animate()
            .rotationBy(360f)
            .setDuration(1000)
            .start()
        //保存密码
        if (orgPassword == null) {
            //设置密码
            if (firstSurePassword == null) {
                firstSurePassword = password.toString()
                mAlert.text = "请确认密码"
            } else {
                //判断两次密码是否一致
                if (firstSurePassword.equals(password.toString())) {
                    mAlert.text = "密码设置成功"
                    SharedPreferenceUtil.getInstance(this).savePassword(firstSurePassword!!)
                } else {
                    mAlert.setTextColor(Color.RED)
                    mAlert.text = "两次密码不一致，请重新设置"
                    firstSurePassword = null
                    loginAnim()
                }
            }
        } else {
            //确认密码
            if (orgPassword.equals(password.toString())) {
                mAlert.text = "密码正确"
            } else {
                mAlert.setTextColor(Color.RED)
                mAlert.text = "密码错误，请重新绘制"
                loginAnim()
            }
        }

        //清空
        password = null
        Handler().postDelayed({
            mAlert.setTextColor(Color.BLACK)
        }, 1000)
    }

    //密码确认动画
    private fun loginAnim(){

        ObjectAnimator.ofFloat(mAlert,"translationX",5f,-5f,0f).apply {
            duration=200
            start()
        }

    }
}