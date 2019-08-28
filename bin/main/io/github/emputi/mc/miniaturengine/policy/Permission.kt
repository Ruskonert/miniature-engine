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
    fun setBasePermission(p : Permission) {
        this.basePermission = p
    }
    fun isRoot() : Boolean {
        return this.basePermission == null
    }

    fun getSubstantialPermissionBased() : Permission {
        val string = this.getSubstantialPermission()
        return Permission(string)
    }

    fun getSubstantialPermission() : String {
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
        return sender.hasPermission(this.getSubstantialPermission())
    }
}