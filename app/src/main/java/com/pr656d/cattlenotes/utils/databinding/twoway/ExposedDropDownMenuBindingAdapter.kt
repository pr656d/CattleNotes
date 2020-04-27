package com.pr656d.cattlenotes.utils.databinding.twoway

import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.pr656d.cattlenotes.R

/**
 * Custom two way data binding adapter for [AutoCompleteTextView] to show drop down list
 * and select from it.
 */
@BindingAdapter("dropDownText")
fun setDropDownText(view: AutoCompleteTextView, newText: String?) {
    if (view.text?.toString() != newText)
        view.setText(newText, false)
}

@InverseBindingAdapter(attribute = "dropDownText")
fun getDropDownText(view: AutoCompleteTextView): String? {
    return view.text?.toString()
}

@BindingAdapter(
    value = [
        "dropDownList",
        "onItemClickListener",
        "dropDownTextAttrChanged"
    ], requireAll = false
)
fun setListeners(
    view: AutoCompleteTextView,
    list: Array<String>,
    listener: AdapterView.OnItemClickListener?,
    attrChange: InverseBindingListener?
) {

    /** Sort the list */
    val dropDownList: List<String> = list.sorted()

    view.setAdapter(
        ArrayAdapter(
            view.context,
            R.layout.item_dropdown_menu_popup,
            dropDownList
        )
    )

    if (attrChange == null) {
        view.onItemClickListener = listener
    } else {
        view.setOnItemClickListener { parent, v, position, id ->
            listener?.onItemClick(parent, v, position, id)
            view.setText(dropDownList[position], false)
            attrChange.onChange()
        }
    }
}

@BindingAdapter(
    value = [
        "dropDownList",
        "onItemClickListener",
        "dropDownTextAttrChanged"
    ], requireAll = false
)
fun setListeners(
    view: AutoCompleteTextView,
    dropDownListId: Int?,
    listener: AdapterView.OnItemClickListener?,
    attrChange: InverseBindingListener?
) {
    dropDownListId ?: return
    val list: Array<String> = view.resources.getStringArray(dropDownListId)
    setListeners(view, list, listener, attrChange)
}