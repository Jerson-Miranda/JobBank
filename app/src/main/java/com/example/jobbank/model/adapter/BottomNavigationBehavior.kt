package com.example.jobbank.model.adapter

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationBehavior : CoordinatorLayout.Behavior<BottomNavigationView>() {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: BottomNavigationView, dependency: View): Boolean {
        // Dependemos del RecyclerView para poder deslizar el BottomNavigationView
        return dependency is RecyclerView
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: BottomNavigationView,
                                     directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        // Si el usuario está deslizando verticalmente en el RecyclerView, iniciamos el desplazamiento del BottomNavigationView
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: BottomNavigationView,
                                   target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        // Si el RecyclerView está desplazándose hacia arriba, ocultamos el BottomNavigationView; si está desplazándose hacia abajo, lo mostramos
        if (dy > 0) {
            hideBottomNavigationView(child)
        } else if (dy < 0) {
            showBottomNavigationView(child)
        }
    }

    private fun hideBottomNavigationView(view: BottomNavigationView) {
        view.animate().translationY(view.height.toFloat())
    }

    private fun showBottomNavigationView(view: BottomNavigationView) {
        view.animate().translationY(0f)
    }

}