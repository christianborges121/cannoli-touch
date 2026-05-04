package dev.cannoli.scorza.launcher

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import dev.cannoli.scorza.config.AppConfig
import dev.cannoli.scorza.config.DataBinding
import dev.cannoli.scorza.config.ExtraSpec
import dev.cannoli.scorza.config.ExtraValueKind
import java.io.File

object EmulatorIntentBuilder {

    fun resolve(context: Context, config: AppConfig, romFile: File): ResolvedIntent {
        val component = config.activity?.let { ComponentName(config.packageName, it) }
        val pkgOnly = if (component == null) config.packageName else null
        val dataUri: Uri? = when (val d = config.data) {
            DataBinding.None -> null
            is DataBinding.FileProvider -> fileProviderUri(context, romFile)
            DataBinding.AbsolutePath -> Uri.fromFile(romFile)
            is DataBinding.CustomScheme -> Uri.parse("${d.scheme}://${d.authority}")
                .buildUpon().appendPath(romFile.absolutePath).build()
        }
        val resolvedExtras = config.extras.map { spec -> resolveExtra(context, spec, romFile) }
        return ResolvedIntent(
            component = component,
            packageName = pkgOnly,
            action = config.action,
            dataUri = dataUri,
            mimeType = if (dataUri != null) config.mimeType else null,
            flagsHex = "0x${Integer.toHexString(config.intentFlags)}",
            extras = resolvedExtras,
        )
    }

    fun toAndroidIntent(context: Context, resolved: ResolvedIntent, config: AppConfig): Intent {
        val intent = Intent(resolved.action).apply {
            resolved.component?.let { component = it } ?: resolved.packageName?.let(::setPackage)
            addFlags(config.intentFlags)
            resolved.dataUri?.let { uri ->
                if (resolved.mimeType != null) setDataAndType(uri, resolved.mimeType)
                else data = uri
            }
        }
        if (intent.component == null && intent.action == Intent.ACTION_MAIN) {
            context.packageManager.getLaunchIntentForPackage(config.packageName)?.component?.let {
                intent.component = it
                intent.setPackage(null)
            }
        }
        if (resolved.dataUri != null && (config.data as? DataBinding.FileProvider)?.grantPermission == true) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        for (extra in resolved.extras) when (extra) {
            is ResolvedExtra.StringExtra -> intent.putExtra(extra.key, extra.value)
            is ResolvedExtra.UriExtra -> {
                intent.putExtra(extra.key, extra.value)
                context.grantUriPermission(config.packageName, extra.value, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
        for (extra in resolved.extras) {
            if (extra is ResolvedExtra.StringExtra && extra.value.startsWith("content://")) {
                context.grantUriPermission(config.packageName, Uri.parse(extra.value), Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
        return intent
    }

    private fun resolveExtra(context: Context, spec: ExtraSpec, romFile: File): ResolvedExtra = when (spec.kind) {
        ExtraValueKind.FILE_PATH ->
            ResolvedExtra.StringExtra(spec.key, romFile.absolutePath)
        ExtraValueKind.FILE_URI_STRING ->
            ResolvedExtra.StringExtra(spec.key, fileProviderUri(context, romFile).toString())
        ExtraValueKind.FILE_URI_PARCELABLE ->
            ResolvedExtra.UriExtra(spec.key, fileProviderUri(context, romFile))
    }

    private fun fileProviderUri(context: Context, romFile: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", romFile)
}
