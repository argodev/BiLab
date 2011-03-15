/**
 * This document is a part of the source code and related artifacts for BiLab,
 * an open source interactive workbench for computational biologists.
 * 
 * http://computing.ornl.gov/
 * 
 * Copyright © 2011 Oak Ridge National Laboratory
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * The license is also available at: http://www.gnu.org/copyleft/lgpl.html
 */
package bilab;

// / interface for notifications
public interface INotifier {
    void DevError(Object from, String message);

    void DevInfo(Object from, String message);

    void DevStatus(Object from, String message);

    void DevWarning(Object from, String message);

    void EndProgress(Object from);

    void LogError(Object from, String message);

    void LogInfo(Object from, String message);

    void PopLevel(Object from);

    void Progress(Object from, double percent);

    void PushLevel(Object from);

    void StartProgress(Object from, String task);

    void UserError(Object from, String message);

    void UserInfo(Object from, String message);

    void UserStatus(Object from, String message);

    void UserWarning(Object from, String message);
}
