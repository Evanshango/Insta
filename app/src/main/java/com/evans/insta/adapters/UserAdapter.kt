package com.evans.insta.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.evans.insta.R
import com.evans.insta.models.User
import com.evans.insta.utils.GlideApp
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private var mContext: Context,
    private var mUser: List<User>,
    private var isFragment: Boolean = false
) : RecyclerView.Adapter<UserAdapter.UserHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false)
        return UserHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val user: User = mUser[position]
        holder.bind(user)
    }

    class UserHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {

        var userNameTxt: TextView = itemView.findViewById(R.id.user_name_search)
        var fullNameTxt: TextView = itemView.findViewById(R.id.user_full_name_search)
        var profileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_search)
        var follow: Button = itemView.findViewById(R.id.btn_follow_search)

        fun bind(user: User) {
            userNameTxt.text = user.getUserName()
            fullNameTxt.text = user.getFullName()

            val profile: String = user.getImage()
            GlideApp.with(itemView.context)
                .load(profile)
                .into(profileImage)
        }
    }

}