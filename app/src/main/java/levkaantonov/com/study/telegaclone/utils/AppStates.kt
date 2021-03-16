package levkaantonov.com.study.telegaclone.utils

enum class AppStates(val state: String) {
    ONLINE("в сети"),
    OFFLINE("был недавно"),
    TYPING("печатает");

    companion object{
        fun updateState(appState: AppStates){
            if(AUTH.currentUser == null){
                return
            }

            REF_DB_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_STATE)
                .setValue(appState.state)
                .addOnSuccessListener { USER.state = appState.state}
                .addOnFailureListener { showToast(it.message.toString()) }
        }
    }
}