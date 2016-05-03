/*
 * Copyright 2016 Pivotal Software, Inc..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.proxyprint.kitchen.models.printshops.items;

/**
 *
 * @author josesousa
 */
public class PaperItem extends Item {
    public static String KEY_BASE = "PAPER";

    protected Format format;
    protected Sides sides;
    protected Colors colors;

    public PaperItem(Format format, Sides sides, Colors colors) {
        this.format = format;
        this.sides = sides;
        this.colors = colors;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public Sides getSides() {
        return sides;
    }

    public void setSides(Sides sides) {
        this.sides = sides;
    }

    public Colors getColors() {
        return colors;
    }

    public void setColors(Colors colors) {
        this.colors = colors;
    }

    // Get the paper specs hash
    public String getPaperSpecs() {
        return this.format.toString()+this.sides.toString();
    }

    @Override
    public String genKey() {
        return String.format("%s,%s,%s,%s", KEY_BASE, colors.toString(), format.toString(), sides.toString());
    }
}
