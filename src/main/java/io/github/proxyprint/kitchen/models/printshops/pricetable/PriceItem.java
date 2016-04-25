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
package io.github.proxyprint.kitchen.models.printshops.pricetable;

import java.util.Objects;

/**
 *
 * @author josesousa
 */
public class PriceItem extends PaperItem {

    private int inLim;
    private int supLim;

    public PriceItem(Format format, Sides sides, Colors colors, int inLim, int supLim) {
        super(format, sides, colors);
        this.inLim = inLim;
        this.supLim = supLim;
    }

    public int getInLim() {
        return inLim;
    }

    public void setInLim(int inLim) {
        this.inLim = inLim;
    }

    public int getSupLim() {
        return supLim;
    }

    public void setSupLim(int supLim) {
        this.supLim = supLim;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%d,%d", this.colors, this.format, this.sides, inLim, supLim);
    }

}
