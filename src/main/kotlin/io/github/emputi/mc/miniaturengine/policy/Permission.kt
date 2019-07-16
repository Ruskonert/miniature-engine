package io.github.emputi.mc.miniaturengine.policy

import org.bukkit.command.CommandSender

class Permission {
    private val element : String

    constructor(element : String) {
        this.element = element
    }

    constructor(element : String, base : Permission) {
        this.element = element
        this.basePermission = base
    }

    private var basePermission : Permission? = null
    fun isRoot() : Boolean {
        return this.basePermission == null
    }

    fun getCombinedPermission() : Permission {
        val string = this.getPermission()
        return Permission(string)
    }

    fun getPermission() : String {
        if(this.isRoot()) {
            return this.element
        }
        var permissionString = this.element
        var targetPermission: Permission? = this.basePermission

        while(targetPermission != null) {
            permissionString = targetPermission.element + "." + permissionString
            targetPermission = targetPermission.basePermission
        }
        return permissionString
    }

    fun hasPermission(sender : CommandSender) : Boolean {
        return sender.hasPermission(this.getPermission())
    }
}