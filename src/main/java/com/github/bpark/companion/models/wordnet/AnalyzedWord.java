/*
 * Copyright 2017 Kurt Sparber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.bpark.companion.models.wordnet;

import java.util.List;

@SuppressWarnings("unused")
public class AnalyzedWord {

    private String stem;

    private String lemma;

    private String gloss;

    private String lexicalName;

    private String lexialDescription;

    private List<String> synonyms;

    private List<String> hypernyms;


    public String getStem() {
        return stem;
    }

    public String getLemma() {
        return lemma;
    }

    public String getGloss() {
        return gloss;
    }

    public String getLexicalName() {
        return lexicalName;
    }

    public String getLexialDescription() {
        return lexialDescription;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public List<String> getHypernyms() {
        return hypernyms;
    }

}
