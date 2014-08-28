/*
 * This file is part of Technic Launcher Core.
 * Copyright (C) 2013 Syndicate, LLC
 *
 * Technic Launcher Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Launcher Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.technicpack.launchercore.modpacks.resources;

import net.technicpack.launchercore.auth.UserModel;
import net.technicpack.launchercore.image.IImageStore;
import net.technicpack.launchercore.mirror.MirrorStore;
import net.technicpack.launchercore.modpacks.ModpackModel;
import net.technicpack.launchercore.modpacks.resources.resourcetype.IModpackResourceType;
import net.technicpack.rest.io.Resource;
import net.technicpack.utilslib.Utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class PackImageStore implements IImageStore<ModpackModel> {
    private IModpackResourceType resourceType;
    private MirrorStore mirrorStore;
    private UserModel userModel;

    public PackImageStore(IModpackResourceType resourceType, MirrorStore mirrorStore, UserModel userModel) {
        this.resourceType = resourceType;
        this.mirrorStore = mirrorStore;
        this.userModel = userModel;
    }

    @Override
    public void downloadImage(ModpackModel key, File target) {
        Resource res = resourceType.getResource(key);

        if (res == null || res.getUrl() == null || res.getUrl().isEmpty() || (key.getPackInfo() != null &&!key.getPackInfo().isComplete()))
            return;

        try {
            mirrorStore.downloadFile(res.getUrl(), userModel.getCurrentUser().getDisplayName(), target.getAbsolutePath());
        } catch (IOException e) {
            Utils.getLogger().log(Level.INFO, "Error downloading pack resource "+res.getUrl()+" for pack "+key.getName(), e);
        }
    }

    @Override
    public String getJobKey(ModpackModel key) {
        if (key.getPackInfo() == null || key.getPackInfo().isComplete())
            return "pack-resource-"+key.getName()+"-"+resourceType.getImageName();
        else
            return "null-image";
    }
}
