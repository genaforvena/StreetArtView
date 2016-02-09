package org.imozerov.streetartview.ui.helper

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

/**
 * Created by imozerov on 09.02.16.
 */
fun AppCompatActivity.replaceFragment(id : Int, fragment: Fragment) {
    supportFragmentManager.beginTransaction().replace(id, fragment).commit();
}