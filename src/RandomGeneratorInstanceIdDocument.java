/**
 * The MIT License (MIT)
 * <p>
 * Copyright for portions of project uuid-generator-plugin are held by Léo Millon, 2015.
 * All other copyright for project instanceid-generator-plugin are held by Eric Boivin, 2017.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;

import java.util.HashSet;
import java.util.Random;

public class RandomGeneratorInstanceIdDocument extends AnAction {

    private static final String TAG_TO_REPLACE_START  = "instance-id=\"";
    private static final String TAG_TO_REPLACE_END  = "\"";

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getData(CommonDataKeys.PROJECT);
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);

        if (project == null || editor == null) {
            return;
        }

        //New instance of Runnable to make a replacement
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String entireXmlDoc = editor.getDocument().getCharsSequence().toString();
                entireXmlDoc = updateInstanceIdWithUniqueId(entireXmlDoc);
                editor.getDocument().setText(entireXmlDoc);

            }
        };
        //Making the replacement
        WriteCommandAction.runWriteCommandAction(project, runnable);
    }

    private static String updateInstanceIdWithUniqueId(String entireXmlDocument) {
        HashSet<String> uniqueIdList = new HashSet<String>();

        int lookIndex = 0;
        while (lookIndex != -1 && entireXmlDocument.indexOf(TAG_TO_REPLACE_START, lookIndex) != -1) {
            int beginIndex = entireXmlDocument.indexOf(TAG_TO_REPLACE_START, lookIndex);
            int endIndex = entireXmlDocument.indexOf(TAG_TO_REPLACE_END, beginIndex + TAG_TO_REPLACE_START.length());
            String instanceId = entireXmlDocument.substring(beginIndex + TAG_TO_REPLACE_START.length(), endIndex);

            String uniqueId = null;
            do {
                uniqueId = getUniqueId();
            } while (uniqueIdList.contains(uniqueId));

            uniqueIdList.add(uniqueId);

            entireXmlDocument = entireXmlDocument.replaceFirst(TAG_TO_REPLACE_START + instanceId + TAG_TO_REPLACE_END, TAG_TO_REPLACE_START + uniqueId + TAG_TO_REPLACE_END);

            lookIndex = endIndex;
        }
        System.out.println(entireXmlDocument);
        return entireXmlDocument;
    }

    private static String getUniqueId() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
}
